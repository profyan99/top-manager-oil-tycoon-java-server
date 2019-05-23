package com.topmanager.oiltycoon.game.service.impl;

import com.topmanager.oiltycoon.game.dao.PlayerDao;
import com.topmanager.oiltycoon.game.dto.response.DisconnectRoomDto;
import com.topmanager.oiltycoon.game.dto.response.PlayerConnectDto;
import com.topmanager.oiltycoon.game.dto.response.PlayerDisconnectDto;
import com.topmanager.oiltycoon.game.dto.response.PlayerInfoDto;
import com.topmanager.oiltycoon.game.model.GameState;
import com.topmanager.oiltycoon.game.model.Player;
import com.topmanager.oiltycoon.game.model.Requirement;
import com.topmanager.oiltycoon.game.model.Room;
import com.topmanager.oiltycoon.game.service.RoomEventHandler;
import com.topmanager.oiltycoon.game.service.RoomRunnable;
import com.topmanager.oiltycoon.game.service.RoomService;
import com.topmanager.oiltycoon.social.model.GameStats;
import com.topmanager.oiltycoon.social.security.exception.RestException;
import com.topmanager.oiltycoon.social.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.topmanager.oiltycoon.game.model.GameState.*;
import static com.topmanager.oiltycoon.social.security.exception.ErrorCode.*;

public class RoomProcessor implements RoomRunnable {
    private static final Logger logger = LoggerFactory.getLogger(RoomProcessor.class);
    private Room roomData;
    private RoomEventHandler roomEventHandler;
    private UserService userService;
    private PlayerDao playerDao;
    private int currentSecond;
    private int prepareSecond;
    private PasswordEncoder passwordEncoder;
    private final int TIME_USER_RELOAD;

    public RoomProcessor(RoomProcessorParams processorParams) {
        this.roomData = processorParams.getRoomData();
        this.roomEventHandler = processorParams.getRoomEventHandler();
        this.passwordEncoder = processorParams.getPasswordEncoder();
        this.TIME_USER_RELOAD = processorParams.getRoomData().getRoomPeriodDelay() * 2;
        this.playerDao = processorParams.playerDao;
        this.userService = processorParams.userService;
        prepareSecond = TIME_USER_RELOAD;
        logger.debug("Players:");
        roomData.getPlayers().values().forEach(player -> logger.debug(player.getUserName()));
        initGame();
    }

    public Room getRoomData() {
        return roomData;
    }

    @Override
    public void roomUpdate() {
        if (roomData.getState() == PLAY) {
            currentSecond++;
        }

        if (prepareSecond != 0) {
            prepareSecond--;
        }

        long secondsNow = LocalDateTime.now().getSecond();
        roomData.getPlayers()
                .values()
                .removeIf(player -> {
                    boolean delete = !player.isConnected()
                            && player.getTimeEndReload() != 0
                            && secondsNow > player.getTimeEndReload();
                    if (delete) {
                        player.setTimeEndReload(0);
                        deleteInactivePlayer(player);
                    }
                    return delete;
                });

        if ((roomData.getState() == END || roomData.getState() == PREPARE)
                && roomData.getCurrentPlayers() == 0 && roomData.getPlayers().isEmpty()
                && prepareSecond <= 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("Room [" + getRoomData().getName() + "] :: " + "no players in room. Delete room");
            }
            roomEventHandler.deleteRoom(roomData.getId());
        }

        if (currentSecond == roomData.getRoomPeriodDelay()) {
            currentSecond = 0;
            roomData.setCurrentRound(Math.min(roomData.getCurrentRound() + 1, roomData.getMaxRounds()));
            if (logger.isDebugEnabled()) {
                logger.debug("Room [" + getRoomData().getName() + "] :: " + "new round ["
                        + roomData.getCurrentRound() + "/" + roomData.getMaxRounds() + "]");
            }
            if (roomData.getCurrentRound() == roomData.getMaxRounds()) {
                endGame();
            } else {
                calcGame();
            }
        }


    }

    @Transactional
    public Optional<PlayerInfoDto> onPlayerConnect(Player player, String password) {
        if (roomData.getPlayers().get(player.getUser().getUserName()) != null) {
            if (roomData.isLocked() && !passwordEncoder.matches(password, roomData.getPassword())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Room [" + getRoomData().getName() + "] :: " + "invalid password: " + player.getUser().getUserName());
                }
                throw new RestException(INVALID_ROOM_PASSWORD);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Room [" + getRoomData().getName() + "] :: " + "User reconnected: " + player.getUser().getUserName());
            }
            Player oldPlayer = roomData.getPlayers().get(player.getUser().getUserName());
            oldPlayer.setConnected(true);
            oldPlayer.setTimeEndReload(0);
            return Optional.empty();
            //TODO reconnect
        } else {
            if (roomData.getCurrentPlayers() == roomData.getMaxPlayers()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Room [" + getRoomData().getName() + "] :: " + "is full: " + player.getUser().getUserName());
                }
                throw new RestException(ROOM_IS_FULL);
            }
            if (roomData.getState() == PLAY) {
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
            player.setRoom(getRoomData());
            playerDao.save(player);
            if (roomData.getCurrentPlayers() == roomData.getMaxPlayers()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Room [" + getRoomData().getName() + "] :: " + "start new game. Enough players.");
                }
                newGame();
            }
            return Optional.of(new PlayerConnectDto(player));
        }
    }

    @Transactional
    public Optional<PlayerInfoDto> onPlayerDisconnect(String playerName, boolean force) {
        Player disconnectedPlayer = roomData.getPlayers().get(playerName);
        if (disconnectedPlayer != null) {
            disconnectedPlayer.setConnected(false);
            if (roomData.getState() == PLAY) {
                if(force) {
                    setPlayerLeaveGame(disconnectedPlayer);
                    playerDao.delete(disconnectedPlayer);
                    roomData.setCurrentPlayers(roomData.getCurrentPlayers() - 1);
                    roomData.getPlayers().remove(playerName);
                } else {
                    disconnectedPlayer.setTimeEndReload(LocalDateTime.now().getSecond() + TIME_USER_RELOAD);
                    playerDao.save(disconnectedPlayer);
                }
            } else {
                playerDao.delete(disconnectedPlayer);
                roomData.setCurrentPlayers(roomData.getCurrentPlayers() - 1);
                roomData.getPlayers().remove(playerName);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Room [" + getRoomData().getName() + "] :: " + "player disconnected: " + disconnectedPlayer.getUser().getUserName());
            }
            return Optional.of(new PlayerDisconnectDto(disconnectedPlayer));
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Room [" + getRoomData().getName() + "] :: " + "disconnect user not found: " + playerName);
            }
        }
        return Optional.empty();
    }

    @Transactional
    void deleteInactivePlayer(Player player) {
        setPlayerLeaveGame(player);
        roomData.setCurrentPlayers(roomData.getCurrentPlayers() - 1);
        playerDao.delete(player);
        if (logger.isDebugEnabled()) {
            logger.debug("Room [" + getRoomData().getName() + "] :: " + "inactive user delete from room : " + player.getUser().getUserName());
        }
        roomEventHandler.updateRoom(roomData.getId());
    }

    private void newGame() {
        roomData.setState(PLAY);
        currentSecond = 0;
        if (logger.isDebugEnabled()) {
            logger.debug("Room [" + getRoomData().getName() + "] :: " + "new game");
        }
        roomEventHandler.updateRoom(roomData.getId());
    }

    private void initGame() {
        roomData.setState(GameState.PREPARE);
        if (logger.isDebugEnabled()) {
            logger.debug("Room [" + getRoomData().getName() + "] :: " + "init game");
        }
    }

    private void endGame() {
        roomData.setState(END);
        roomData.getPlayers()
                .values()
                .removeIf(player -> {
                    boolean delete = !player.isConnected();
                    if (delete) {
                        deleteInactivePlayer(player);
                    }
                    return delete;
                });
        if (logger.isDebugEnabled()) {
            logger.debug("Room [" + getRoomData().getName() + "] :: " + "regular end of game");
        }
        roomEventHandler.updateRoom(roomData.getId());
    }

    private void calcGame() {
        roomEventHandler.updateRoom(roomData.getId());
    }

    private void setPlayerLeaveGame(Player player) {
        userService.setUserLeaveGame(player.getUser());
    }

    @Transactional
    public void onRoomDelete() {
        roomEventHandler.sendRoomEvent(roomData.getId(), new DisconnectRoomDto("Room has been deleted"));
        roomData.getPlayers()
                .values()
                .removeIf(player -> {
                    playerDao.delete(player);
                    return true;
                });
    }


    @AllArgsConstructor
    @Getter
    public static class RoomProcessorParams {
        public UserService userService;
        private Room roomData;
        private RoomEventHandler roomEventHandler;
        private PasswordEncoder passwordEncoder;
        private PlayerDao playerDao;
    }
}
