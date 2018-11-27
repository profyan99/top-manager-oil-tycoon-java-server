package com.topmanager.oiltycoon.game.service.impl;

import com.topmanager.oiltycoon.game.dao.impl.RoomDaoImpl;
import com.topmanager.oiltycoon.game.dto.response.PlayerInfoDto;
import com.topmanager.oiltycoon.game.model.GameState;
import com.topmanager.oiltycoon.game.model.Player;
import com.topmanager.oiltycoon.game.model.Requirement;
import com.topmanager.oiltycoon.game.model.Room;
import com.topmanager.oiltycoon.game.service.RoomRunnable;
import com.topmanager.oiltycoon.game.service.RoomService;
import com.topmanager.oiltycoon.social.model.GameStats;
import com.topmanager.oiltycoon.social.security.exception.RestException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.topmanager.oiltycoon.social.security.exception.ErrorCode.*;

public class RoomProcessor implements RoomRunnable {
    private static final Logger logger = LoggerFactory.getLogger(RoomProcessor.class);
    private Room roomData;
    private RoomService roomService;
    private int currentSecond;
    private PasswordEncoder passwordEncoder;
    private final long TIME_USER_RELOAD;

    public RoomProcessor(RoomProcessorParams processorParams) {
        this.roomData = processorParams.getRoomData();
        this.roomService = processorParams.getRoomService();
        this.passwordEncoder = processorParams.getPasswordEncoder();
        this.TIME_USER_RELOAD = processorParams.getRoomData().getRoomPeriodDelay() * 2;

        initGame();
    }

    public Room getRoomData() {
        return roomData;
    }

    @Override
    public void roomUpdate() {
        currentSecond++;
        long secondsNow = LocalDateTime.now().getSecond();
        roomData.getPlayers()
                .values()
                .removeIf(player -> {
                    boolean delete = !player.isConnected() && secondsNow > player.getTimeEndReload();
                    player.getUser().getGameStats().setLeaveGameAmount(player.getUser().getGameStats().getLeaveGameAmount() + 1);
                    roomService.updateUserGameStats(player.getUser().getGameStats());
                    if (delete) {
                        roomData.setCurrentPlayers(roomData.getCurrentPlayers() - 1);
                        if (logger.isDebugEnabled()) {
                            logger.debug("Room [" + getRoomData().getName() + "] :: " + "User delete from room: " + player.getUser().getUserName());
                        }
                    }
                    return delete;
                });

        if (currentSecond == roomData.getRoomPeriodDelay()) {
            currentSecond = 0;
            roomData.setCurrentRound(roomData.getCurrentRound() + 1);
            if (logger.isDebugEnabled()) {
                logger.debug("Room [" + getRoomData().getName() + "] :: " + "new round ["
                        + roomData.getCurrentRound() + "/" + roomData.getMaxRounds() + "]");
            }
            if(roomData.getCurrentPlayers() == 0 && roomData.getPlayers().isEmpty()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Room [" + getRoomData().getName() + "] :: " + "no players in room. Delete room");
                }
                //TODO force delete room
            }
            if (roomData.getCurrentRound() == roomData.getMaxRounds()) {
                roomData.setState(GameState.END);
                if (logger.isDebugEnabled()) {
                    logger.debug("Room [" + getRoomData().getName() + "] :: " + "regular end of game");
                }
            }
        }
    }

    public void onPlayerConnect(Player player, String password) {
        if (roomData.getPlayers().get(player.getUser().getUserName()) != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Room [" + getRoomData().getName() + "] :: " + "User reconnected: " + player.getUser().getUserName());
            }
            //TODO reconnect
        } else {
            if (roomData.getCurrentPlayers() == roomData.getMaxPlayers()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Room [" + getRoomData().getName() + "] :: " + "is full: " + player.getUser().getUserName());
                }
                throw new RestException(ROOM_IS_FULL);
            }
            if (roomData.getState() == GameState.PLAY) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Room [" + getRoomData().getName() + "] :: " + "already started: " + player.getUser().getUserName());
                }
                throw new RestException(GAME_HAS_ALREADY_STARTED);
            }
            if (roomData.isLocked() && !passwordEncoder.matches(password, roomData.getPassword())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Room [" + getRoomData().getName() + "] :: " + "invalid password: " + player.getUser().getUserName());
                }
                throw new RestException(INVALID_ROOM_PASSWORD);
            }
            if (roomData.getRequirement() != null) {
                Requirement requirement = roomData.getRequirement();
                GameStats userGameStats = player.getUser().getGameStats();
                if (requirement.getMinHoursInGameAmount() > userGameStats.getHoursInGame()
                        && userGameStats.getAchievements().containsAll(requirement.getRequireAchievements())
                        && requirement.getRequireRoles().containsAll(requirement.getRequireRoles())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Room [" + getRoomData().getName() + "] :: " + "not satisfy: " + player.getUser().getUserName());
                    }
                    throw new RestException(PLAYER_NOT_SATISFY);
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Room [" + getRoomData().getName() + "] :: " + "successfully connected: " + player.getUser().getUserName());
            }
            player.setConnected(true);
            roomData.getPlayers().put(player.getUser().getUserName(), player);
            roomData.setCurrentPlayers(roomData.getCurrentPlayers() + 1);
            roomService.sendUserConnect(roomData.getId(), new PlayerInfoDto());
            if (roomData.getCurrentPlayers() == roomData.getMaxPlayers()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Room [" + getRoomData().getName() + "] :: " + "start new game. Enough players.");
                }
                newGame();
            }
        }
    }

    public void onPlayerDisconnect(String playerName) {
        Player disconnectedPlayer = roomData.getPlayers().get(playerName);
        if (disconnectedPlayer != null) {
            disconnectedPlayer.setTimeEndReload(LocalDateTime.now().getSecond() + TIME_USER_RELOAD);
            disconnectedPlayer.setConnected(false);
            roomService.sendUserDisonnect(roomData.getId(), new PlayerInfoDto());
            if (logger.isDebugEnabled()) {
                logger.debug("Room [" + getRoomData().getName() + "] :: " + "player disconnected: " + disconnectedPlayer.getUser().getUserName());
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Room [" + getRoomData().getName() + "] :: " + "disconnect user not found: " + playerName);
            }
        }
    }

    private void newGame() {
        roomData.setState(GameState.PLAY);
        if (logger.isDebugEnabled()) {
            logger.debug("Room [" + getRoomData().getName() + "] :: " + "new game");
        }
    }

    private void initGame() {
        roomData.setState(GameState.PREPARE);
        if (logger.isDebugEnabled()) {
            logger.debug("Room [" + getRoomData().getName() + "] :: " + "init game");
        }
    }


    @AllArgsConstructor
    @Getter
    public static class RoomProcessorParams {
        private Room roomData;
        private RoomService roomService;
        private PasswordEncoder passwordEncoder;
    }
}
