package com.topmanager.oiltycoon.game.service.impl;

import com.topmanager.oiltycoon.game.dao.CompanyDao;
import com.topmanager.oiltycoon.game.dao.PlayerDao;
import com.topmanager.oiltycoon.game.dto.response.GameTickResponseDto;
import com.topmanager.oiltycoon.game.dto.response.PlayerInfoResponseDto;
import com.topmanager.oiltycoon.game.dto.response.ResponseEventType;
import com.topmanager.oiltycoon.game.model.GameState;
import com.topmanager.oiltycoon.game.model.Player;
import com.topmanager.oiltycoon.game.model.PlayerState;
import com.topmanager.oiltycoon.game.model.Room;
import com.topmanager.oiltycoon.game.model.game.Company;
import com.topmanager.oiltycoon.game.model.game.CompanyStatistics;
import com.topmanager.oiltycoon.game.model.game.Store;
import com.topmanager.oiltycoon.game.service.MessageSender;
import com.topmanager.oiltycoon.game.service.RoomRunnable;
import com.topmanager.oiltycoon.social.model.User;
import com.topmanager.oiltycoon.social.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.topmanager.oiltycoon.game.model.GameState.END;
import static com.topmanager.oiltycoon.game.model.GameState.PLAY;

@Service
public class GameService implements RoomRunnable {
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    private final UserService userService;
    private final PlayerDao playerDao;
    private final MessageSender messageSender;
    private final CompanyDao companyDao;

    @Autowired
    public GameService(UserService userService, PlayerDao playerDao, MessageSender messageSender, CompanyDao companyDao) {
        this.userService = userService;
        this.playerDao = playerDao;
        this.messageSender = messageSender;
        this.companyDao = companyDao;
    }

    @Override
    @Transactional
    public boolean roomUpdate(Room room) {
        boolean isSaveNeed;

        if (room.getState() == PLAY) {
            room.incCurrentSecond();
            updateTick(room, room.getCurrentSecond());
        }

        if (room.getPrepareSecond() != 0) {
            room.decPrepareSecond();
            updateTick(room, room.getPrepareSecond());
        }

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
        return isSaveNeed;
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
    public Player onPlayerAdd(Room roomData, User user, String companyName) {
        Player player = createNewPlayer(roomData, user, companyName);
        roomData.addPlayer(player);
        if (roomData.getCurrentPlayers() == roomData.getMaxPlayers()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Room [" + roomData.getName() + "] :: " + "start new game. Enough players.");
            }
            newGame(roomData);
        }
        return player;
    }

    @Transactional
    public void onPlayerRemove(Room roomData, Player disconnectedPlayer) {
        if (roomData.getState() == PLAY) {
            setPlayerLeaveGame(roomData, disconnectedPlayer.getUser());
        }
        roomData.removePlayer(disconnectedPlayer);
    }

    private void newGame(Room roomData) {
        roomData.setState(PLAY);
        roomData.setCurrentSecond(0);
        roomData.setPrepareSecond(0);
        if (logger.isDebugEnabled()) {
            logger.debug("Room [" + roomData.getName() + "] :: " + "new game");
        }
    }

    public void initGame(Room roomData) {
        roomData.setState(GameState.PREPARE);

        Room.GameData gameData = new Room.GameData(
                8400, 3360, 3360, 8400d, 0d, 0, 3360
        );
        roomData.setSendSolutionAllowed(false);
        roomData.getPeriodData().put(roomData.getCurrentRound(), gameData);

        if (logger.isDebugEnabled()) {
            logger.debug("Room [" + roomData.getName() + "] :: " + "init game");
        }
    }

    private void allowSendSolutions(Room roomData) {
        roomData.setSendSolutionAllowed(true);

        roomData.getPlayers().values().forEach(player -> {
            player.setState(PlayerState.THINK);
            messageSender.sendToRoomDest(
                    roomData.getId(),
                    new PlayerInfoResponseDto(ResponseEventType.UPDATE, new PlayerInfoResponseDto.PlayerInfoDto(player))
            );
        });
        //TODO batch
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
        allowSendSolutions(roomData);
    }

    private void setPlayerLeaveGame(Room roomData, User user) {
        userService.setUserLeaveGame(user);
        if (logger.isDebugEnabled()) {
            logger.debug("Room [" + roomData.getName() + "] :: " + "inactive user delete from room : " + user.getUserName());
        }
    }

    private Player createNewPlayer(Room roomData, User user, String companyName) {
        Player player = new Player(user);
        Company company = new Company(
                null,
                player,
                companyName,
                new CompanyStatistics(100, 30, 0, 0, 0, 100d / roomData.getMaxPlayers()),
                new Store(),
                0
        );
        player.setCompany(company);
        companyDao.save(company);
        playerDao.save(player);
        return player;
    }

    private void updateTick(Room room, int amount) {
        messageSender.sendToRoomDest(
                room.getId(),
                new GameTickResponseDto(new GameTickResponseDto.GameTickDto(amount))
        );
    }
}
