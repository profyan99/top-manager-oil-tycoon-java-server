package com.topmanager.oiltycoon.game.service.impl;

import com.topmanager.oiltycoon.game.dao.CompanyDao;
import com.topmanager.oiltycoon.game.dao.PlayerDao;
import com.topmanager.oiltycoon.game.dto.CompanyDto;
import com.topmanager.oiltycoon.game.dto.request.PlayerSolutionsDto;
import com.topmanager.oiltycoon.game.dto.response.*;
import com.topmanager.oiltycoon.game.model.GameState;
import com.topmanager.oiltycoon.game.model.Player;
import com.topmanager.oiltycoon.game.model.PlayerState;
import com.topmanager.oiltycoon.game.model.Room;
import com.topmanager.oiltycoon.game.model.game.GamePeriodData;
import com.topmanager.oiltycoon.game.model.game.company.*;
import com.topmanager.oiltycoon.game.model.game.scenario.Scenario;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.topmanager.oiltycoon.game.model.GameState.END;
import static com.topmanager.oiltycoon.game.model.GameState.PLAY;

@Service
public class GameService implements RoomRunnable {
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    private final UserService userService;
    private final ComputationService computationService;
    private final PlayerDao playerDao;
    private final MessageSender messageSender;
    private final CompanyDao companyDao;

    @Autowired
    public GameService(UserService userService, ComputationService computationService, PlayerDao playerDao, MessageSender messageSender, CompanyDao companyDao) {
        this.userService = userService;
        this.computationService = computationService;
        this.playerDao = playerDao;
        this.messageSender = messageSender;
        this.companyDao = companyDao;
    }

    @Override
    @Transactional
    public boolean roomUpdate(Room room) {
        boolean isSaveNeed;

        if (room.getState() == PLAY) {
            sendUpdateTick(room, room.incCurrentSecond());
        }

        if (room.getPrepareSecond() != 0) {
            sendUpdateTick(room, room.decPrepareSecond());
        }

        if (room.getCurrentSecond() == room.getRoomPeriodDelay()) {
            handleNewPeriod(room);
            isSaveNeed = true;
        } else {
            isSaveNeed = removeInactivePlayers(room);
        }
        return isSaveNeed;
    }

    private boolean removeInactivePlayers(Room roomData) {
        final long secondsNow = Instant.now().getEpochSecond();
        List<Player> playersToDelete = roomData
                .getPlayers()
                .values()
                .stream()
                .filter(player ->
                        !player.isConnected()
                                && (player.getTimeEndReload() != 0
                                && secondsNow > player.getTimeEndReload()
                                || roomData.getState() == END)
                )
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
            startGame(roomData);
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

    private void startGame(Room roomData) {
        roomData.setState(PLAY);
        roomData.setPrepareSecond(0);

        handleNewPeriod(roomData);

        if (logger.isDebugEnabled()) {
            logger.debug("Room [" + roomData.getName() + "] :: " + "new game");
        }
    }

    public void initGame(Room roomData) {
        roomData.setState(GameState.PREPARE);

        GamePeriodData gameData = new GamePeriodData();
        roomData.setSendSolutionAllowed(false);
        roomData.getPeriodData().put(-1, gameData);

        if (logger.isDebugEnabled()) {
            logger.debug("Room [" + roomData.getName() + "] :: " + "init game");
        }
    }

    private void allowSendSolutions(Room roomData) {
        roomData.setSendSolutionAllowed(true);

        for (Player player : roomData.getPlayers().values()) {
            Company company = player.getCompany();
            CompanyPeriodData companyPeriodData = new CompanyPeriodData(roomData.getCurrentPeriod());
            company.addDataByPeriod(roomData.getCurrentPeriod(), companyPeriodData);

            player.setState(PlayerState.THINK);
        }
    }

    public boolean onPlayerSendSolutions(Room roomData, Player player, PlayerSolutionsDto solutionsDto) {
        if (player.isSolutionsSent() || !roomData.isSendSolutionAllowed() || player.isBankrupt()) {
            return false;
        }

        CompanyPeriodData currentData = player.getCompany().getDataByPeriod(roomData.getCurrentPeriod());
        Scenario scenario = roomData.getScenario();
        int availableMoney =
                scenario.getLoanLimit() + scenario.getExtraLoanLimit() + currentData.getBank() - currentData.getLoan();

        int expenses = (int) (solutionsDto.getInvestments() + solutionsDto.getMarketing() + solutionsDto.getNir()
                + currentData.getProductionCost() * solutionsDto.getProduction());

        if (expenses > availableMoney) {
            expenses -= solutionsDto.getMarketing();
            solutionsDto.setMarketing(0);
        }

        if (expenses > availableMoney) {
            expenses -= solutionsDto.getNir();
            solutionsDto.setNir(0);
        }

        if (expenses > availableMoney) {
            expenses -= currentData.getProductionCost() * solutionsDto.getProduction();
            solutionsDto.setProduction(0);
        }

        if (expenses > availableMoney) {
            expenses -= solutionsDto.getInvestments();
            solutionsDto.setInvestments(0);
        }

        if (expenses > availableMoney) {
            player.setBankrupt(true);
        }

        CompanySolutions companySolutions = new CompanySolutions(
                solutionsDto.getPrice(),
                solutionsDto.getProduction(),
                solutionsDto.getMarketing(),
                solutionsDto.getInvestments(),
                solutionsDto.getNir()
        );
        currentData.setSolutions(companySolutions);
        player.setSolutionsSent(true);
        player.setState(PlayerState.WAIT);

        playerDao.save(player);
        messageSender.sendToRoomDest(
                roomData.getId(),
                new PlayerInfoResponseDto(ResponseEventType.UPDATE, new PlayerInfoResponseDto.PlayerInfoDto(player))
        );

        long bankruptAmount = roomData.getPlayers().values().stream().filter(Player::isBankrupt).count();
        if (roomData.incPlayersSolutionSentAmount() + bankruptAmount >= roomData.getCurrentPlayers()) {
            handleNewPeriod(roomData);
            return true;
        }
        return false;
    }

    private void handleNewPeriod(Room roomData) {
        roomData.setCurrentSecond(0);
        roomData.setSendSolutionAllowed(false);
        roomData.setPlayersSolutionSentAmount(0);

        //calculates all game period and every company
        calcGame(roomData);

        if (roomData.getCurrentPeriod() == roomData.getMaxRounds()) {
            endGame(roomData);
        } else {
            roomData.setCurrentPeriod(Math.min(roomData.getCurrentPeriod() + 1, roomData.getMaxRounds()));
            allowSendSolutions(roomData);
        }
        // update game and player's state and rating
        sendUpdateGame(roomData);
        // update every players company
        for (Player player : roomData.getPlayers().values()) {
            Company company = player.getCompany();
            companyDao.save(company);
            playerDao.save(player);

            messageSender.sendToUserDest(
                    player.getUserName(),
                    new CompanyResponseDto(
                            ResponseEventType.UPDATE,
                            new CompanyDto(company.getDataByPeriod(roomData.getCurrentPeriod()))
                    )
            );
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Room [" + roomData.getName() + "] :: " + "new round ["
                    + roomData.getCurrentPeriod() + "/" + roomData.getMaxRounds() + "]");
        }
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

        GamePeriodData newPeriodData = computationService.calculatePeriod(roomData, roomData.getCurrentPeriod());
        roomData.addPeriodDataByPeriod(roomData.getCurrentPeriod() + 1, newPeriodData);
    }

    private void setPlayerLeaveGame(Room roomData, User user) {
        userService.setUserLeaveGame(user);
        if (logger.isDebugEnabled()) {
            logger.debug("Room [" + roomData.getName() + "] :: " + "inactive user delete from room : " + user.getUserName());
        }
    }

    private Player createNewPlayer(Room roomData, User user, String companyName) {
        int playersAmount = roomData.getMaxPlayers();

        Player player = new Player(user);
        player.setBankrupt(false);
        player.setState(PlayerState.WAIT);
        player.setSolutionsSent(false);
        player.setConnected(true);
        player.setRoom(roomData);

        Map<Integer, CompanyPeriodData> periodDataMap = new HashMap<>();
        CompanyPeriodData initial = new CompanyPeriodData(
                -1,
                new CompanyStore(
                        3600 / playersAmount,
                        4200 / playersAmount,
                        0,
                        0,
                        0,
                        0,
                        0
                ),
                new CompanySolutions(
                        30,
                        3360 / playersAmount,
                        8400 / playersAmount,
                        4200 / playersAmount * 2,
                        3360 / playersAmount
                ),
                85870 / playersAmount,
                72240 / playersAmount,
                4200 / playersAmount,
                18d,
                4200 / playersAmount
        );
        CompanyPeriodData zero = new CompanyPeriodData();
        zero.setSolutions(new CompanySolutions(
                30,
                3360 / playersAmount,
                8400 / playersAmount,
                4200 / playersAmount * 2,
                3360 / playersAmount
        ));

        periodDataMap.put(-1, initial);
        periodDataMap.put(0, zero);
        Company company = new Company(null, player, companyName, periodDataMap);
        player.setCompany(company);
        companyDao.save(company);
        playerDao.save(player);
        return player;
    }

    private void sendUpdateTick(Room room, int amount) {
        messageSender.sendToRoomDest(
                room.getId(),
                new GameTickResponseDto(new GameTickResponseDto.GameTickDto(amount))
        );
    }

    private void sendUpdateGame(Room room) {
        messageSender.sendToRoomDest(
                room.getId(),
                new GameInfoResponseDto(ResponseEventType.UPDATE, new GameInfoResponseDto.GameInfoDto(room))
        );
    }
}
