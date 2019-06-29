package com.topmanager.oiltycoon.game.service.impl;

import com.topmanager.oiltycoon.game.dao.PlayerDao;
import com.topmanager.oiltycoon.game.dao.RoomDao;
import com.topmanager.oiltycoon.game.dto.request.RoomAddDto;
import com.topmanager.oiltycoon.game.dto.request.RoomConnectDto;
import com.topmanager.oiltycoon.game.dto.response.GameInfoResponseDto;
import com.topmanager.oiltycoon.game.dto.response.PlayerInfoResponseDto;
import com.topmanager.oiltycoon.game.dto.response.ResponseEventType;
import com.topmanager.oiltycoon.game.model.Room;
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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.topmanager.oiltycoon.social.security.exception.ErrorCode.ROOM_NAME_NOT_UNIQUE;

@Service
public class RoomService {
    private final RoomDao roomDao;
    private final PlayerDao playerDao;
    private final GameService gameService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    private Map<String, Integer> playerRoomIdMap;
    private Map<String, String> subscriptionDestinationMap;

    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);

    @Autowired
    public RoomService(RoomDao roomDao, PlayerDao playerDao, PasswordEncoder passwordEncoder,
                       UserService userService, GameService gameService) {
        this.roomDao = roomDao;
        this.playerDao = playerDao;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.gameService = gameService;

        playerRoomIdMap = new HashMap<>();
        subscriptionDestinationMap = new HashMap<>();
    }

    @Transactional
    public void addRoom(RoomAddDto roomAdd) {
        if (roomDao.existsByName(roomAdd.getName())) {
            throw new RestException(ROOM_NAME_NOT_UNIQUE);
        }
        Room room = new Room(
                roomAdd.getName(),
                roomAdd.getMaxPlayers(),
                roomAdd.isLocked(),
                roomAdd.isTournament(),
                roomAdd.isScenario(),
                roomAdd.getScenarioName(),
                roomAdd.getRequirement(),
                roomAdd.getMaxRounds(),
                roomAdd.isLocked() ? passwordEncoder.encode(roomAdd.getPassword()) : null,
                roomAdd.getRoomPeriodDelay()
        );
        gameService.initGame(room);
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
        gameService.onPlayerConnect(currentRoom, user, roomConnectDto.getPassword());
        return new GameInfoResponseDto(ResponseEventType.ADD,
                new GameInfoResponseDto.GameInfoDto(
                        roomId,
                        currentRoom.getName(),
                        currentRoom.getMaxPlayers(),
                        currentRoom.getCurrentPlayers(),
                        currentRoom.isLocked(),
                        currentRoom.isTournament(),
                        currentRoom.isScenario(),
                        currentRoom.getScenario(),
                        currentRoom.getState(),
                        currentRoom.getMaxRounds(),
                        currentRoom.getCurrentRound(),
                        currentRoom
                                .getPlayers()
                                .values()
                                .stream()
                                .map(p -> new PlayerInfoResponseDto.PlayerInfoDto(
                                        p.getUserName(),
                                        p.getUser().getAvatar(),
                                        p.getUser().getId()
                                ))
                                .collect(Collectors.toList())

                ));
    }

    public void connectToRoomWebsocket(int roomId, SimpMessageHeaderAccessor accessor) {
        User user = userService.getUser();
        if (playerDao
                .findByUser(user)
                .orElseThrow(() -> new RestException(ErrorCode.INVALID_ROOM_ID))
                .getRoom()
                .getId() != roomId) {
            throw new RestException(ErrorCode.INVALID_ROOM_ID);
        }
        playerRoomIdMap.put(accessor.getSessionId(), roomId);
    }

    @Transactional
    public void deleteRoom(int roomId) {
        Room room = roomDao.findById(roomId)
                .orElseThrow(() -> new RestException(ErrorCode.INVALID_ROOM_ID));
        gameService.onRoomDelete(room);
    }


    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        logger.debug(":: Session connected\nname: " + headers.getUser().getName() + "\nDest: " + headers.getDestination());
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        int roomId = playerRoomIdMap.getOrDefault(headerAccessor.getSessionId(), -1);
        logger.debug(":: Session disconnect\nroomId: " + roomId + "\nname: " + headerAccessor.getUser().getName());
        if (roomId != -1) {
            Room room = roomDao.findById(roomId)
                    .orElseThrow(() -> new RestException(ErrorCode.INVALID_ROOM_ID));
            gameService.onPlayerDisconnect(room, headerAccessor.getUser().getName(), false);
            playerRoomIdMap.remove(headerAccessor.getSessionId());
        }
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        logger.debug(":: Session subscribe\nname: " + headers.getUser().getName() + "\nDest: " + headers.getDestination());
        subscriptionDestinationMap.put(headers.getSubscriptionId(), headers.getDestination());
    }

    @EventListener
    public void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        String sub = subscriptionDestinationMap.getOrDefault(headers.getSubscriptionId(), "-1");
        logger.debug(":: Session unsubscribe\nname: " + headers.getUser().getName() + "\nsub: " + sub);
        int roomId = extractRoomIdFromDestination(sub);
        if (roomId != -1) {
            Room room = roomDao.findById(roomId)
                    .orElseThrow(() -> new RestException(ErrorCode.INVALID_ROOM_ID));

            gameService.onPlayerDisconnect(room, headers.getUser().getName(), true);
            playerRoomIdMap.remove(headers.getSessionId());
            subscriptionDestinationMap.remove(headers.getSubscriptionId());
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
}
