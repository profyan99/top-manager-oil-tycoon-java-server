package com.topmanager.oiltycoon.game.service.impl;

import com.topmanager.oiltycoon.game.dao.CompanyDao;
import com.topmanager.oiltycoon.game.dao.PlayerDao;
import com.topmanager.oiltycoon.game.dto.request.PlayerSolutionsDto;
import com.topmanager.oiltycoon.game.dto.response.GameInfoResponseDto;
import com.topmanager.oiltycoon.game.dto.response.GameTickResponseDto;
import com.topmanager.oiltycoon.game.dto.response.PlayerInfoResponseDto;
import com.topmanager.oiltycoon.game.dto.response.ResponseEventType;
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

        sendUpdateGame(roomData, ResponseEventType.START);
        allowSendSolutions(roomData);

        if (logger.isDebugEnabled()) {
            logger.debug("Room [" + roomData.getName() + "] :: " + "new game");
        }
    }

    public void initGame(Room roomData) {
        roomData.setState(GameState.PREPARE);

        GamePeriodData gameData = new GamePeriodData(
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
            playerDao.save(player);
            messageSender.sendToRoomDest(
                    roomData.getId(),
                    new PlayerInfoResponseDto(ResponseEventType.UPDATE, new PlayerInfoResponseDto.PlayerInfoDto(player))
            );
        });
    }

    public boolean onPlayerSendSolutions(Room roomData, Player player, PlayerSolutionsDto solutionsDto) {
        if (player.isSolutionsSent() || !roomData.isSendSolutionAllowed() || player.isBankrupt()) {
            return false;
        }

        CompanyPeriodData currentData = player.getCompany().getDataByPeriod(roomData.getCurrentRound());
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
        roomData.setCurrentRound(Math.min(roomData.getCurrentRound() + 1, roomData.getMaxRounds()));
        roomData.setSendSolutionAllowed(false);
        roomData.setPlayersSolutionSentAmount(0);

        if (logger.isDebugEnabled()) {
            logger.debug("Room [" + roomData.getName() + "] :: " + "new round ["
                    + roomData.getCurrentRound() + "/" + roomData.getMaxRounds() + "]");
        }
        if (roomData.getCurrentRound() == roomData.getMaxRounds()) {
            endGame(roomData);
        } else {
            calcGame(roomData);
        }
    }

    private void endGame(Room roomData) {
        roomData.setState(END);
        if (logger.isDebugEnabled()) {
            logger.debug("Room [" + roomData.getName() + "] :: " + "regular end of game");
        }
        sendUpdateGame(roomData, ResponseEventType.END);
    }

    private void calcGame(Room roomData) {
        if (logger.isDebugEnabled()) {
            logger.debug("Room [" + roomData.getName() + "] :: " + "calculation round");
        }

        //TODO send industry summary
        allowSendSolutions(roomData);
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
        Map<Integer, CompanyPeriodData> periodDataMap = new HashMap<>();
        CompanyPeriodData zeroPeriodData = new CompanyPeriodData(
                null,
                roomData.getCurrentRound(),
                new CompanyStatistics(
                        100,
                        30,
                        0,
                        0,
                        0,
                        100d / playersAmount
                ),
                new CompanyStore(
                        3600 / playersAmount,
                        4200 / playersAmount,
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
                72240 / playersAmount,0,0,0,0,0,0,0,0,0,0,0,
                4200/playersAmount,0,0d,0,0,
                18d,0
        );
        computationService.calculateCompany(zeroPeriodData, roomData);

        periodDataMap.put(roomData.getCurrentRound(), zeroPeriodData);
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

    private void sendUpdateGame(Room room, ResponseEventType responseEventType) {
        messageSender.sendToRoomDest(
                room.getId(),
                new GameInfoResponseDto(responseEventType, new GameInfoResponseDto.GameInfoDto(room))
        );
    }
}
