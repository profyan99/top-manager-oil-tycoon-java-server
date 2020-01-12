package com.topmanager.oiltycoon.game.service.impl;

import com.topmanager.oiltycoon.game.dao.CompanyDao;
import com.topmanager.oiltycoon.game.dao.PlayerDao;
import com.topmanager.oiltycoon.game.dto.request.RoomChatMessageRequestDto;
import com.topmanager.oiltycoon.game.dto.response.*;
import com.topmanager.oiltycoon.game.model.GameState;
import com.topmanager.oiltycoon.game.model.Player;
import com.topmanager.oiltycoon.game.model.Requirement;
import com.topmanager.oiltycoon.game.model.Room;
import com.topmanager.oiltycoon.game.model.game.Company;
import com.topmanager.oiltycoon.game.model.game.Store;
import com.topmanager.oiltycoon.game.service.MessageSender;
import com.topmanager.oiltycoon.game.service.RoomRunnable;
import com.topmanager.oiltycoon.social.model.GameStats;
import com.topmanager.oiltycoon.social.model.User;
import com.topmanager.oiltycoon.social.security.exception.RestException;
import com.topmanager.oiltycoon.social.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.topmanager.oiltycoon.game.model.GameState.END;
import static com.topmanager.oiltycoon.game.model.GameState.PLAY;
import static com.topmanager.oiltycoon.social.security.exception.ErrorCode.*;

@Service
public class GameService implements RoomRunnable {
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    private final UserService userService;
    private final PlayerDao playerDao;
    private final PasswordEncoder passwordEncoder;
    private final MessageSender messageSender;
    private final RoomListService roomListService;
    private final CompanyDao companyDao;

    @Autowired
    public GameService(UserService userService, PlayerDao playerDao, PasswordEncoder passwordEncoder,
                       MessageSender messageSender, RoomListService roomListService, CompanyDao companyDao) {
        this.userService = userService;
        this.playerDao = playerDao;
        this.passwordEncoder = passwordEncoder;
        this.messageSender = messageSender;
        this.roomListService = roomListService;
        this.companyDao = companyDao;
    }

    @Override
    @Transactional
    public void roomUpdate(Room room) {
        boolean isSaveNeed = false;

        if (room.getState() == PLAY) {
            room.incCurrentSecond();
            updateTick(room, room.getCurrentSecond());
        }

        if (room.getPrepareSecond() != 0) {
            room.decPrepareSecond();
            updateTick(room, room.getPrepareSecond());
        }

        if (room.getCurrentPlayers() == 0
                && room.getPlayers().isEmpty()
                && room.getPrepareSecond() <= 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("Room [" + room.getName() + "] :: " + "no players in room. Delete room");
            }
            this.onRoomDelete(room);
        } else {
            if (room.getCurrentSecond() == room.getRoomPeriodDelay()) {
                room.setCurrentSecond(0);
                room.setCurrentRound(Math.min(room.getCurrentRound() + 1, room.getMaxRounds()));
                if (logger.isDebugEnabled()) {
                    logger.debug("Room [" + room.getName() + "] :: " + "new round ["
                            + room.getCurrentRound() + "/" + room.getMaxRounds() + "]");
                }
                if (room.getCurrentRound() == room.getMaxRounds()) {
                    endGame(room);
                } else {
                    calcGame(room);
                }
                isSaveNeed = true;
            } else {
                final long secondsNow = Instant.now().getEpochSecond();
                isSaveNeed = removePlayers(room, player ->
                        !player.isConnected()
                                && (player.getTimeEndReload() != 0
                                && secondsNow > player.getTimeEndReload()
                                || room.getState() == END)
                );
            }
        }
        if (isSaveNeed) {
            updateRoom(room);
        }
    }

    private boolean removePlayers(Room roomData, Predicate<Player> condition) {
        List<Player> playersToDelete = roomData
                .getPlayers()
                .values()
                .stream()
                .filter(condition)
                .collect(Collectors.toList());

        if (playersToDelete.size() > 0) {
            logger.debug("Room [" + roomData.getName() + "] :: " + "delete inactive players: [" + playersToDelete.size() + "]");
            playersToDelete.forEach(player -> {
                setPlayerLeaveGame(roomData, player.getUser());
                roomData.removePlayer(player);
            });
            return true;
        }
        return false;
    }

    @Transactional
    public void onPlayerConnect(Room roomData, User user, String password, String companyName) {
        if (roomData.getPlayers().get(user.getUserName()) != null) {
            if (roomData.isLocked() && !passwordEncoder.matches(password, roomData.getPassword())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Room [" + roomData.getName() + "] :: " + "invalid password: " + user.getUserName());
                }
                throw new RestException(INVALID_ROOM_PASSWORD);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Room [" + roomData.getName() + "] :: " + "User reconnected: " + user.getUserName());
            }
            Player oldPlayer = roomData.getPlayers().get(user.getUserName());
            oldPlayer.setConnected(true);
            oldPlayer.setTimeEndReload(0);
            messageSender.sendToRoomDest(
                    roomData.getId(),
                    new PlayerReconnectResponseDto(oldPlayer)
            );
        } else {
            if (roomData.getCurrentPlayers() == roomData.getMaxPlayers()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Room [" + roomData.getName() + "] :: " + "is full: " + user.getUserName());
                }
                throw new RestException(ROOM_IS_FULL);
            }
            if (roomData.getState() == PLAY) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Room [" + roomData.getName() + "] :: " + "already started: " + user.getUserName());
                }
                throw new RestException(GAME_HAS_ALREADY_STARTED);
            }
            if (roomData.isLocked() && !passwordEncoder.matches(password, roomData.getPassword())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Room [" + roomData.getName() + "] :: " + "invalid password: " + user.getUserName());
                }
                throw new RestException(INVALID_ROOM_PASSWORD);
            }
            if (roomData.getRequirement() != null) {
                Requirement requirement = roomData.getRequirement();
                GameStats userGameStats = user.getGameStats();
                if (requirement.getMinHoursInGameAmount() > userGameStats.getHoursInGame()
                        && userGameStats.getAchievements().containsAll(requirement.getRequireAchievements())
                        && requirement.getRequireRoles().containsAll(requirement.getRequireRoles())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Room [" + roomData.getName() + "] :: " + "not satisfy: " + user.getUserName());
                    }
                    throw new RestException(PLAYER_NOT_SATISFY);
                }
            }
            if (roomData.getPlayers().values().stream().anyMatch(p -> p.getCompany().getName().equals(companyName))) {
                throw new RestException(COMPANY_NAME_ALREADY_EXISTS);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Room [" + roomData.getName() + "] :: " + "successfully connected: " + user.getUserName());
            }
            Player player = createNewPlayer(user, companyName);
            roomData.addPlayer(player);
            if (roomData.getCurrentPlayers() == roomData.getMaxPlayers()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Room [" + roomData.getName() + "] :: " + "start new game. Enough players.");
                }
                newGame(roomData);
            } else {
                updateRoom(roomData);
            }
            messageSender.sendToRoomDest(
                    roomData.getId(),
                    new PlayerConnectResponseDto(player)
            );

        }
    }

    @Transactional
    public void onPlayerDisconnect(Room roomData, String playerName, boolean force) {
        Player disconnectedPlayer = roomData.getPlayers().get(playerName);
        if (disconnectedPlayer != null) {
            User user = disconnectedPlayer.getUser();
            disconnectedPlayer.setConnected(false);

            if (roomData.getState() == PLAY) {
                if (force) {
                    setPlayerLeaveGame(roomData, disconnectedPlayer.getUser());
                    roomData.removePlayer(disconnectedPlayer);
                    roomListService.updateRoom(roomData);
                } else {
                    disconnectedPlayer.setTimeEndReload(Instant.now().getEpochSecond() + roomData.getTimePlayerReload());
                    playerDao.save(disconnectedPlayer);
                }
            } else {
                roomData.removePlayer(disconnectedPlayer);
                roomListService.updateRoom(roomData);
            }
            logger.debug(
                    "Room [" + roomData.getName() + "] :: " + "player disconnected: "
                            + user.getUserName() + " force: " + force
            );
            messageSender.sendToRoomDest(roomData.getId(), new PlayerDisconnectResponseDto(
                    new PlayerDisconnectResponseDto.PlayerDisconnectDto(disconnectedPlayer, force)
            ));
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Room [" + roomData.getName() + "] :: " + "disconnect user not found: " + playerName);
            }
        }
    }

    private void newGame(Room roomData) {
        roomData.setState(PLAY);
        roomData.setCurrentSecond(0);
        roomData.setPrepareSecond(0);
        if (logger.isDebugEnabled()) {
            logger.debug("Room [" + roomData.getName() + "] :: " + "new game");
        }
        updateRoom(roomData);
    }

    public void initGame(Room roomData) {
        roomData.setState(GameState.PREPARE);

        Room.GameData gameData = new Room.GameData(
                0,
                0,
                0,
                roomData.getMaxPlayers(),
                97.5d,
                1d
        );
        roomData.getPeriodData().put(roomData.getCurrentRound(), gameData);

        if (logger.isDebugEnabled()) {
            logger.debug("Room [" + roomData.getName() + "] :: " + "init game");
        }
        roomListService.addRoom(roomData);
    }

    private void endGame(Room roomData) {
        roomData.setState(END);
        if (logger.isDebugEnabled()) {
            logger.debug("Room [" + roomData.getName() + "] :: " + "regular end of game");
        }
    }

    private void calcGame(Room roomData) {
        if (logger.isDebugEnabled()) {
            logger.debug("Room [" + roomData.getName() + "] :: " + "calculation round");
        }
    }

    private void setPlayerLeaveGame(Room roomData, User user) {
        userService.setUserLeaveGame(user);
        if (logger.isDebugEnabled()) {
            logger.debug("Room [" + roomData.getName() + "] :: " + "inactive user delete from room : " + user.getUserName());
        }
    }

    @Transactional
    public void onRoomDelete(Room roomData) {
        messageSender.sendToRoomDest(roomData.getId(), new ServerMessageResponseDto(
                ResponseEventType.REMOVE, new ServerMessageResponseDto.ServerMessageDto("Комната будет удалена.")
        ));
        roomListService.deleteRoom(roomData);
    }

    @Transactional(readOnly = true)
    public void onChatMessageSend(Room roomData, RoomChatMessageRequestDto chatMessageDto, Player player) {
        messageSender.sendToRoomDest(chatMessageDto.getRoomId(), new ChatMessageResponseDto(
                new ChatMessageResponseDto.ChatMessageDto(
                        chatMessageDto.getMessage(),
                        new PlayerInfoResponseDto.PlayerInfoDto(
                                player.getUserName(),
                                player.getUser().getAvatar(),
                                player.getCompany().getName(),
                                player.getUser().getId()
                        ),
                        LocalDateTime.now())
        ));

    }

    private Player createNewPlayer(User user, String companyName) {
        Player player = new Player(user);
        Company company = new Company(null, player, companyName, new Store(), 0, 0);
        player.setCompany(company);
        companyDao.save(company);
        playerDao.save(player);
        return player;
    }

    private void updateRoom(Room room) {
        roomListService.updateRoom(room);
        messageSender.sendToRoomDest(
                room.getId(),
                new GameInfoResponseDto(ResponseEventType.UPDATE, new GameInfoResponseDto.GameInfoDto(room))
        );
    }

    private void updateTick(Room room, int amount) {
        messageSender.sendToRoomDest(
                room.getId(),
                new GameTickResponseDto(new GameTickResponseDto.GameTickDto(amount))
        );
    }
}
