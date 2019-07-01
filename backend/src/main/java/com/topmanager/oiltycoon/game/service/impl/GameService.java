package com.topmanager.oiltycoon.game.service.impl;

import com.topmanager.oiltycoon.game.dao.PlayerDao;
import com.topmanager.oiltycoon.game.dto.response.*;
import com.topmanager.oiltycoon.game.model.GameState;
import com.topmanager.oiltycoon.game.model.Player;
import com.topmanager.oiltycoon.game.model.Requirement;
import com.topmanager.oiltycoon.game.model.Room;
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

    @Autowired
    public GameService(UserService userService, PlayerDao playerDao, PasswordEncoder passwordEncoder,
                       MessageSender messageSender, RoomListService roomListService) {
        this.userService = userService;
        this.playerDao = playerDao;
        this.passwordEncoder = passwordEncoder;
        this.messageSender = messageSender;
        this.roomListService = roomListService;
    }

    @Override
    @Transactional
    public void roomUpdate(Room room) {
        if (room.getState() == PLAY) {
            room.incCurrentSecond();
        }

        if (room.getPrepareSecond() != 0) {
            room.decPrepareSecond();
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
            } else {
                final long secondsNow = Instant.now().getEpochSecond();
                removePlayers(room, player ->
                        !player.isConnected()
                                && (player.getTimeEndReload() != 0
                                && secondsNow > player.getTimeEndReload()
                                || room.getState() == END)
                );
            }
        }

    }

    private void removePlayers(Room roomData, Predicate<Player> condition) {
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
            roomListService.updateRoom(roomData);
        }
    }

    @Transactional
    public void onPlayerConnect(Room roomData, User user, String password) {
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
                    new PlayerReconnectResponseDto(
                            oldPlayer.getUserName(),
                            oldPlayer.getUser().getAvatar(),
                            oldPlayer.getUser().getId()
                    )
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
            if (logger.isDebugEnabled()) {
                logger.debug("Room [" + roomData.getName() + "] :: " + "successfully connected: " + user.getUserName());
            }
            Player player = new Player(user);
            player.setConnected(true);
            roomData.addPlayer(player);
            playerDao.save(player);
            if (roomData.getCurrentPlayers() == roomData.getMaxPlayers()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Room [" + roomData.getName() + "] :: " + "start new game. Enough players.");
                }
                newGame(roomData);
            } else {
                roomListService.updateRoom(roomData);
            }
            messageSender.sendToRoomDest(
                    roomData.getId(),
                    new PlayerConnectResponseDto(
                            player.getUserName(),
                            player.getUser().getAvatar(),
                            player.getUser().getId()
                    )
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
                    new PlayerDisconnectResponseDto.PlayerDisconnectDto(
                            user.getUserName(),
                            user.getAvatar(),
                            user.getId(),
                            force
                    )
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
        roomListService.updateRoom(roomData);
    }

    public void initGame(Room roomData) {
        roomData.setState(GameState.PREPARE);
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
        roomListService.updateRoom(roomData);
    }

    private void calcGame(Room roomData) {
        if (logger.isDebugEnabled()) {
            logger.debug("Room [" + roomData.getName() + "] :: " + "calculation round");
        }
        roomListService.updateRoom(roomData);
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
}
