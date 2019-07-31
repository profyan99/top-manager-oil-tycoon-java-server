package com.topmanager.oiltycoon.game.service.impl;

import com.topmanager.oiltycoon.game.dao.CompanyDao;
import com.topmanager.oiltycoon.game.dao.PlayerDao;
import com.topmanager.oiltycoon.game.dto.CompanyDto;
import com.topmanager.oiltycoon.game.dto.response.*;
import com.topmanager.oiltycoon.game.model.GameState;
import com.topmanager.oiltycoon.game.model.Player;
import com.topmanager.oiltycoon.game.model.Requirement;
import com.topmanager.oiltycoon.game.model.Room;
import com.topmanager.oiltycoon.game.model.game.*;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                    new PlayerReconnectResponseDto(
                            oldPlayer.getUserName(),
                            oldPlayer.getUser().getAvatar(),
                            oldPlayer.getUser().getId(),
                            new CompanyDto(oldPlayer.getCompany())
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
                roomListService.updateRoom(roomData);
            }
            messageSender.sendToRoomDest(
                    roomData.getId(),
                    new PlayerConnectResponseDto(
                            player.getUserName(),
                            player.getUser().getAvatar(),
                            player.getUser().getId(),
                            new CompanyDto(player.getCompany())
                    )
            );

        }
    }

    @Transactional
    public void onPlayerDisconnect(Room roomData, String playerName, boolean force) {
        Player disconnectedPlayer = roomData.getPlayers().get(playerName);
        if (disconnectedPlayer != null) {
            User user = disconnectedPlayer.getUser();
            Company company = disconnectedPlayer.getCompany();
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
                            new CompanyDto(company),
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

        Room.GameData gameData = new Room.GameData(
                0,
                0,
                0,
                roomData.getMaxPlayers(),
                Arrays.stream(OilType.values()).collect(Collectors.toMap(p -> p, p -> 97.5d)),
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

    private Player createNewPlayer(User user, String companyName) {
        Player player = new Player(user);

        Company company = new Company(
                null,
                player,
                companyName,
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new Store(0, new HashMap<>(), new HashMap<>()),
                1_000_000,
                0,
                0

        );
        Factory factory = new Factory(null, null, new HashMap<>());
        calcFactory(factory, 0, 0, 0);
        company.addFactory(factory);

        GasStation gasStation = new GasStation(null, null, new HashMap<>());
        calcGasStation(
                gasStation,
                0,
                0,
                Arrays.stream(OilType.values()).collect(Collectors.toMap(p -> p, p -> 97.5d)),
                0
        );
        company.addGasStation(gasStation);

        OilWell oilWell = new OilWell(null, null, 0, new HashMap<>());
        calcOilWell(oilWell, 0, 0);
        company.addOilWell(oilWell);

        player.setCompany(company);
        companyDao.save(company);
        playerDao.save(player);
        return player;
    }

    private void calcFactory(Factory factory, int investments, int nir, int currentRound) {
        Factory.FactoryData newFactoryData;
        if (currentRound == 0) {
            newFactoryData = new Factory.FactoryData(
                    nir, investments, 2_000_000,
                    Arrays.stream(OilType.values()).collect(Collectors.toMap(p -> p, p -> 30_000)),
                    Arrays.stream(OilType.values()).collect(Collectors.toMap(p -> p, p -> 130 * 16.5d / 60d))
            );
        } else {
            Factory.FactoryData oldFactoryData = factory.getPeriodData().get(currentRound - 1);
            newFactoryData = new Factory.FactoryData(
                    oldFactoryData.getNir() + nir,
                    oldFactoryData.getInvestments() + investments,
                    oldFactoryData.getCost(),
                    oldFactoryData.getProductionPower()
                            .entrySet()
                            .stream()
                            .peek(entry -> entry.setValue(
                                    (int) Math.sqrt(30_000d * 30_000d
                                            + Math.sqrt(oldFactoryData.getInvestments() * Math.pow(10d, 12d)))
                            ))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                    oldFactoryData.getProductCostPrice()
                            .entrySet()
                            .stream()
                            .peek(entry -> {
                                entry.setValue(130 * 16.5d / 60d);
                            })
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
            );
        }
        factory.getPeriodData().put(currentRound, newFactoryData);
    }

    private void calcGasStation(GasStation gasStation, int marketing, int image, Map<OilType, Double> oilPrice,
                                int currentRound) {
        GasStation.GasStationData newData;
        if (currentRound == 0) {
            newData = new GasStation.GasStationData(
                    marketing, image, 1_000_000,
                    oilPrice
            );
        } else {
            GasStation.GasStationData oldData = gasStation.getPeriodData().get(currentRound - 1);
            if (oilPrice.values().stream().anyMatch(price -> price <= 0)) {
                throw new RestException(INVALID_PRODUCT_PRICE);
            }
            newData = new GasStation.GasStationData(
                    marketing,
                    oldData.getImage() + image,
                    oldData.getCost(),
                    oilPrice
            );
        }
        gasStation.getPeriodData().put(currentRound, newData);
    }

    private void calcOilWell(OilWell oilWell, int nir, int currentRound) {
        OilWell.OilWellData newData;
        if (currentRound == 0) {
            newData = new OilWell.OilWellData(
                    nir,
                    18_000,
                    2_500_000,
                    17.5d
            );
        } else {
            OilWell.OilWellData oldData = oilWell.getPeriodData().get(currentRound - 1);
            newData = new OilWell.OilWellData(
                    oldData.getNir() + nir,
                    (int) Math.sqrt(40_000 + oldData.getNir() / 28d),
                    oldData.getCost(),
                    0.013d * (Math.pow(oilWell.getStartPeriod(), 2d)) + 17.5d
            );
        }
        oilWell.getPeriodData().put(currentRound, newData);
    }
}
