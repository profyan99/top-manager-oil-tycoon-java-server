package com.topmanager.oiltycoon.game.service.impl;

import com.topmanager.oiltycoon.Utils;
import com.topmanager.oiltycoon.game.dao.PlayerDao;
import com.topmanager.oiltycoon.game.dao.RoomDao;
import com.topmanager.oiltycoon.game.dao.ScenarioDao;
import com.topmanager.oiltycoon.game.dto.request.*;
import com.topmanager.oiltycoon.game.dto.response.*;
import com.topmanager.oiltycoon.game.model.Player;
import com.topmanager.oiltycoon.game.model.Requirement;
import com.topmanager.oiltycoon.game.model.Room;
import com.topmanager.oiltycoon.game.model.game.scenario.Scenario;
import com.topmanager.oiltycoon.game.service.MessageSender;
import com.topmanager.oiltycoon.social.model.GameStats;
import com.topmanager.oiltycoon.social.model.User;
import com.topmanager.oiltycoon.social.security.exception.ErrorCode;
import com.topmanager.oiltycoon.social.security.exception.RestException;
import com.topmanager.oiltycoon.social.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.topmanager.oiltycoon.game.model.GameState.PLAY;
import static com.topmanager.oiltycoon.social.security.exception.ErrorCode.*;

@Service
public class RoomService {
    private final RoomDao roomDao;
    private final PlayerDao playerDao;
    private final ScenarioDao scenarioDao;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final GameService gameService;
    private final RoomListService roomListService;
    private final MessageSender messageSender;

    private Map<String, Integer> playerRoomIdMap;
    private Map<String, String> subscriptionDestinationMap;

    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);

    @Autowired
    public RoomService(RoomDao roomDao, PlayerDao playerDao, ScenarioDao scenarioDao, PasswordEncoder passwordEncoder,
                       UserService userService, GameService gameService, RoomListService roomListService, MessageSender messageSender) {
        this.roomDao = roomDao;
        this.playerDao = playerDao;
        this.scenarioDao = scenarioDao;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.gameService = gameService;
        this.roomListService = roomListService;
        this.messageSender = messageSender;

        playerRoomIdMap = new HashMap<>();
        subscriptionDestinationMap = new HashMap<>();
    }

    public Set<ScenarioResponseDto> getScenarios() {
        return scenarioDao.findAll().stream()
                .map(ScenarioResponseDto::new)
                .collect(Collectors.toSet());
    }

    public ScenarioResponseDto addScenario(ScenarioAddRequestDto requestDto) {
        if (scenarioDao.findByName(requestDto.getName()).isPresent()) {
            throw new RestException(ErrorCode.SCENARIO_NAME_MUST_BE_UNIQUE);
        }

        Scenario scenario = new Scenario(
                null,
                requestDto.getName(),
                requestDto.getDescription(),
                requestDto.getLoanLimit(),
                requestDto.getExtraLoanLimit(),
                requestDto.getBankRate(),
                requestDto.getExtraBankRate()
        );
        scenario = scenarioDao.save(scenario);
        return new ScenarioResponseDto(scenario);
    }

    @Transactional
    public void addRoom(RoomAddDto roomAdd) {
        if (roomDao.existsByName(roomAdd.getName())) {
            throw new RestException(ROOM_NAME_NOT_UNIQUE);
        }

        Scenario scenario;
        if (roomAdd.getScenarioName().isEmpty()) {
            scenario = scenarioDao.findByName(Utils.DEFAULT_SCENARIO)
                    .orElseThrow(() -> new RestException(ErrorCode.UNABLE_TO_ADD_ROOM));
        } else {
            scenario = scenarioDao.findByName(roomAdd.getScenarioName())
                    .orElseThrow(() -> new RestException(ErrorCode.INVALID_SCENARIO));
        }

        Room room = new Room(
                roomAdd.getName(),
                roomAdd.getMaxPlayers(),
                roomAdd.isLocked(),
                roomAdd.isTournament(),
                roomAdd.isScenario(),
                scenario,
                roomAdd.getRequirement(),
                roomAdd.getMaxRounds(),
                roomAdd.isLocked() ? passwordEncoder.encode(roomAdd.getPassword()) : null,
                roomAdd.getRoomPeriodDelay()
        );
        gameService.initGame(room);
        roomDao.save(room);
        roomListService.addRoom(room);
        logger.debug(":: Add room: " + roomAdd.getName());
    }

    @Transactional
    public GameInfoResponseDto connectToRoom(int roomId, RoomConnectDto roomConnectDto) {
        User user = userService.getUser();
        Room currentRoom = roomDao.findById(roomId)
                .orElseThrow(() -> new RestException(ErrorCode.INVALID_ROOM_ID));

        roomDao.findByPlayersUser(user).ifPresent(room -> {
            if (roomId != room.getId()) {
                throw new RestException(ErrorCode.ALREADY_IN_ANOTHER_ROOM);
            }
        });
        if (currentRoom.getPlayers().get(user.getUserName()) != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Room [" + currentRoom.getName() + "] :: " + "User reconnected: " + user.getUserName());
            }
            Player oldPlayer = currentRoom.getPlayers().get(user.getUserName());
            oldPlayer.setConnected(true);
            oldPlayer.setTimeEndReload(0);
            messageSender.sendToRoomDest(currentRoom.getId(), new PlayerReconnectResponseDto(oldPlayer));
        } else {
            if (currentRoom.getCurrentPlayers() == currentRoom.getMaxPlayers()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Room [" + currentRoom.getName() + "] :: " + "is full: " + user.getUserName());
                }
                throw new RestException(ROOM_IS_FULL);
            }
            if (currentRoom.getState() == PLAY) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Room [" + currentRoom.getName() + "] :: " + "already started: " + user.getUserName());
                }
                throw new RestException(GAME_HAS_ALREADY_STARTED);
            }
            if (currentRoom.isLocked() && !passwordEncoder.matches(roomConnectDto.getPassword(), currentRoom.getPassword())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Room [" + currentRoom.getName() + "] :: " + "invalid password: " + user.getUserName());
                }
                throw new RestException(INVALID_ROOM_PASSWORD);
            }
            if (currentRoom.getRequirement() != null) {
                Requirement requirement = currentRoom.getRequirement();
                GameStats userGameStats = user.getGameStats();
                if (requirement.getMinHoursInGameAmount() > userGameStats.getHoursInGame()
                        && userGameStats.getAchievements().containsAll(requirement.getRequireAchievements())
                        && requirement.getRequireRoles().containsAll(requirement.getRequireRoles())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Room [" + currentRoom.getName() + "] :: " + "not satisfy: " + user.getUserName());
                    }
                    throw new RestException(PLAYER_NOT_SATISFY);
                }
            }
            if (currentRoom.getPlayers().values().stream().anyMatch(p -> p.getCompany().getName().equals(roomConnectDto.getCompanyName()))) {
                throw new RestException(COMPANY_NAME_ALREADY_EXISTS);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Room [" + currentRoom.getName() + "] :: " + "successfully connected: " + user.getUserName());
            }
            Player newPlayer = gameService.onPlayerAdd(currentRoom, user, roomConnectDto.getCompanyName());
            updateRoom(currentRoom);
            messageSender.sendToRoomDest(currentRoom.getId(), new PlayerConnectResponseDto(newPlayer));
        }
        return new GameInfoResponseDto(ResponseEventType.ADD, new GameInfoResponseDto.GameInfoDto(currentRoom));
    }

    @Transactional(readOnly = true)
    public void connectToRoomWebsocket(int roomId, SimpMessageHeaderAccessor accessor) {
        User user = userService.getUser();
        checkPlayerInRoom(user, roomId);
        playerRoomIdMap.put(accessor.getSessionId(), roomId);
        logger.debug(":: Connect to room via websocket user: " + user.getUserName() + " room: " + roomId);
    }

    @Transactional
    public void deleteRoom(int roomId) {
        Room room = roomDao.findById(roomId)
                .orElseThrow(() -> new RestException(ErrorCode.INVALID_ROOM_ID));

        roomDao.delete(room);
        messageSender.sendToRoomDest(room.getId(), new ServerMessageResponseDto(
                ResponseEventType.REMOVE, new ServerMessageResponseDto.ServerMessageDto("Комната будет удалена.")
        ));
        roomListService.deleteRoom(room);
        logger.debug(":: Delete room: " + roomId);
    }

    @Transactional
    public void updateRoom(Room room) {
        logger.debug("Room [" + room.getName() + "] :: " + "update");
        roomListService.updateRoom(room);
        roomDao.save(room);
    }

    @Transactional(readOnly = true)
    public void sendChatMessage(RoomChatMessageRequestDto chatMessageDto) {
        Room room = roomDao.findById(chatMessageDto.getRoomId())
                .orElseThrow(() -> new RestException(ErrorCode.INVALID_ROOM_ID));
        User currentUser = userService.getUser();
        checkPlayerInRoom(currentUser, room.getId());
        messageSender.sendToRoomDest(chatMessageDto.getRoomId(), new ChatMessageResponseDto(
                new ChatMessageResponseDto.ChatMessageDto(
                        chatMessageDto.getMessage(),
                        new UserInfoResponseDto.UserInfoDto(currentUser),
                        LocalDateTime.now()
                ))
        );
    }

    @Transactional
    public void setSolutions(int roomId, PlayerSolutionsDto solutionsDto) {
        Room room = roomDao.findById(roomId)
                .orElseThrow(() -> new RestException(ErrorCode.INVALID_ROOM_ID));
        Player player = checkPlayerInRoom(userService.getUser(), roomId);
        if (gameService.onPlayerSendSolutions(room, player, solutionsDto)) {
            updateRoom(room);
        }
    }

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        logger.debug(":: Session connected name: " + headers.getUser().getName() + " Dest: " + headers.getDestination());
    }

    @EventListener
    @Transactional
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        int roomId = playerRoomIdMap.getOrDefault(headerAccessor.getSessionId(), -1);
        logger.debug(":: Session disconnect roomId: " + roomId + " name: " + headerAccessor.getUser().getName());
        if (roomId != -1) {
            Room room = roomDao.findById(roomId)
                    .orElseThrow(() -> new RestException(ErrorCode.INVALID_ROOM_ID));
            onPlayerDisconnect(room, headerAccessor.getUser().getName(), false);
            playerRoomIdMap.remove(headerAccessor.getSessionId());
        }
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        logger.debug(":: Session subscribe name: " + headers.getUser().getName() + " Dest: " + headers.getDestination());
        subscriptionDestinationMap.put(headers.getSubscriptionId(), headers.getDestination());
    }

    @EventListener
    @Transactional
    public void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        String sub = subscriptionDestinationMap.getOrDefault(headers.getSubscriptionId(), "-1");
        logger.debug(":: Session unsubscribe name: " + headers.getUser().getName() + " sub: " + sub);
        int roomId = extractRoomIdFromDestination(sub);
        if (roomId != -1) {
            Room room = roomDao.findById(roomId)
                    .orElseThrow(() -> new RestException(ErrorCode.INVALID_ROOM_ID));

            onPlayerDisconnect(room, headers.getUser().getName(), true);
            playerRoomIdMap.remove(headers.getSessionId());
            subscriptionDestinationMap.remove(headers.getSubscriptionId());
        }
    }

    private void onPlayerDisconnect(Room roomData, String playerName, boolean force) {
        Player disconnectedPlayer = roomData.getPlayers().get(playerName);
        if (disconnectedPlayer != null) {
            User user = disconnectedPlayer.getUser();
            disconnectedPlayer.setConnected(false);

            if (force || roomData.getState() != PLAY) {
                gameService.onPlayerRemove(roomData, disconnectedPlayer);
            } else {
                disconnectedPlayer.setTimeEndReload(Instant.now().getEpochSecond() + roomData.getTimePlayerReload());
                playerDao.save(disconnectedPlayer);
            }

            logger.debug(
                    "Room [" + roomData.getName() + "] :: " + "player disconnected: " + user.getUserName() + " force: " + force
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

    private Integer extractRoomIdFromDestination(String destination) {
        int k;
        if ((k = destination.lastIndexOf("/room/event/")) != -1) {
            String[] paths = destination.split("/");
            return Integer.parseInt(paths[4]);
        } else {
            return k;
        }
    }

    private Player checkPlayerInRoom(User user, int roomId) {
        Player player = playerDao
                .findByUser(user)
                .orElseThrow(() -> new RestException(ErrorCode.INVALID_ROOM_ID));

        if (!player.getRoom().getId().equals(roomId)) {
            throw new RestException(ErrorCode.INVALID_ROOM_ID);
        }
        return player;
    }
}
