package com.topmanager.oiltycoon.game.service;

import com.topmanager.oiltycoon.game.dao.PlayerDao;
import com.topmanager.oiltycoon.game.dao.RoomDao;
import com.topmanager.oiltycoon.game.dto.request.RoomAddDto;
import com.topmanager.oiltycoon.game.dto.request.RoomConnectDto;
import com.topmanager.oiltycoon.game.dto.response.*;
import com.topmanager.oiltycoon.game.model.Player;
import com.topmanager.oiltycoon.game.model.Room;
import com.topmanager.oiltycoon.game.service.impl.RoomProcessor;
import com.topmanager.oiltycoon.social.model.User;
import com.topmanager.oiltycoon.social.security.exception.ErrorCode;
import com.topmanager.oiltycoon.social.security.exception.RestException;
import com.topmanager.oiltycoon.social.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.topmanager.oiltycoon.game.model.RoomDestination.*;
import static com.topmanager.oiltycoon.social.security.exception.ErrorCode.ROOM_NAME_NOT_UNIQUE;
import static com.topmanager.oiltycoon.social.security.exception.ErrorCode.ROOM_NOT_FOUND;

@Service
public class RoomService implements RoomEventHandler{
    private Map<Integer, RoomProcessor> rooms = new HashMap<>();
    private RoomDao roomDao;
    private PlayerDao playerDao;
    private SimpMessagingTemplate messagingTemplate;
    private RoomSchedulingService roomSchedulingService;

    private PasswordEncoder passwordEncoder;
    private UserService userService;

    private Map<String, Integer> playerRoomIdMap;

    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);

    @Autowired
    public RoomService(RoomDao roomDao, PlayerDao playerDao, SimpMessagingTemplate messagingTemplate,
                       RoomSchedulingService roomSchedulingService, PasswordEncoder passwordEncoder, UserService userService) {
        this.roomDao = roomDao;
        this.playerDao = playerDao;
        this.messagingTemplate = messagingTemplate;
        this.roomSchedulingService = roomSchedulingService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        playerRoomIdMap = new HashMap<>();
    }

    @PostConstruct
    public void init() {
        rooms.putAll(
                StreamSupport.stream(roomDao.findAll().spliterator(), false)
                        .map((room) -> new RoomProcessor(new RoomProcessor.RoomProcessorParams(userService, room, this, passwordEncoder, playerDao)))
                        .peek(roomProcessor -> roomSchedulingService.addRoomRunnable(roomProcessor))
                        .collect(Collectors.toMap(
                                roomProcessor -> roomProcessor.getRoomData().getId(),
                                roomProcessor -> roomProcessor)
                        )
        );

    }

    @Transactional(readOnly = true)
    public List<RoomInfoDto> getRoomsList() {
        return rooms.values()
                .stream()
                .map(roomProcessor -> convertRoomIntoDto(roomProcessor.getRoomData()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addRoom(RoomAddDto roomAdd) {
        if (rooms
                .values()
                .stream()
                .anyMatch((roomProcessor -> roomProcessor.getRoomData().getName().equals(roomAdd.getName())))) {
            throw new RestException(ROOM_NAME_NOT_UNIQUE);
        }
        logger.debug("AddRoom: " + roomAdd.toString());
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
        room = roomDao.save(room);
        RoomProcessor roomProcessor = new RoomProcessor(
                new RoomProcessor.RoomProcessorParams(userService, room, this, passwordEncoder, playerDao)
        );
        rooms.put(room.getId(), roomProcessor);
        roomSchedulingService.addRoomRunnable(roomProcessor);
        updateRoomList();
    }

    @Transactional
    public GameInfoDto connectToRoom(RoomConnectDto roomConnectDto, SimpMessageHeaderAccessor accessor) {
        User user = userService.getUser();
        RoomProcessor currentRoom = rooms.get(roomConnectDto.getRoomId());
        if (currentRoom == null) {
            throw new RestException(ErrorCode.INVALID_ROOM_ID);
        }
        playerDao.findByUser(user).ifPresent(player -> {
            if(player.getRoom().getId() != roomConnectDto.getRoomId()) {
                throw new RestException(ErrorCode.ALREADY_IN_ANOTHER_ROOM);
            }
        });
        currentRoom.onPlayerConnect(
                new Player(user),
                roomConnectDto.getPassword()
        ).ifPresent(playerInfoDto -> sendRoomEvent(roomConnectDto.getRoomId(), playerInfoDto));
        updateRoomList();
        playerRoomIdMap.put(accessor.getSessionId(), roomConnectDto.getRoomId());
        return new GameInfoDto(
                currentRoom
                        .getRoomData()
                        .getPlayers()
                        .values()
                        .stream()
                        .map(p -> new PlayerInfoDto(p, ResponseDtoType.GAME_INFO))
                        .collect(Collectors.toList()),
                roomConnectDto.getRoomId()
        );
    }

    @Transactional
    public void deleteRoom(int roomId) {
        RoomProcessor roomProcessor = rooms.get(roomId);
        Room deletedRoom;
        if(roomProcessor != null) {
            roomProcessor.onRoomDelete();
            roomSchedulingService.removeRoomRunnable(rooms.get(roomId));
            deletedRoom = roomProcessor.getRoomData();
            rooms.remove(roomId);
            updateRoomList();
        } else {
            deletedRoom = roomDao.findById(roomId).orElseThrow(
                    () -> new RestException(ROOM_NOT_FOUND)
            );
        }
        roomDao.delete(deletedRoom);
    }

    @Override
    public void sendRoomEvent(int roomId, BaseRoomResponseDto responseDto) {
        messagingTemplate.convertAndSend(
                BROKER_DESTINATION_PREFIX + ROOM_EVENT + "/" + roomId,
                responseDto
        );
    }

    @Transactional
    @Override
    public void updateRoom(int roomId) {
        Room room = rooms.get(roomId).getRoomData();
        roomDao.save(room);
        updateRoomList();
        //TODO patch room upd
    }

    @Transactional(readOnly = true)
    void updateRoomList() {
        logger.info(":: Update room list");
        messagingTemplate.convertAndSend(BROKER_DESTINATION_PREFIX + ROOM_LIST,
                rooms
                        .values()
                        .stream()
                        .map(roomProcessor -> convertRoomIntoDto(roomProcessor.getRoomData()))
                        .collect(Collectors.toList())
        );
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
            rooms.get(roomId).onPlayerDisconnect(headerAccessor.getUser().getName(), false).ifPresent(
                    playerInfoDto -> sendRoomEvent(roomId, playerInfoDto)
            );
            playerRoomIdMap.remove(headerAccessor.getSessionId());
        }
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        logger.debug(":: Session subscribe\nname: " + headers.getUser().getName() + "\nDest: " + headers.getDestination());

    }

    @EventListener
    public void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        logger.debug(":: Session unsubscribe\nname: " + headers.getUser().getName() + "\nDest: " + headers.getDestination());
        int roomId = playerRoomIdMap.getOrDefault(headers.getSessionId(), -1);
        //TODO https://stackoverflow.com/questions/54658349/detect-destination-channel-of-sessionunsubscribeevent
        if (headers.containsNativeHeader("room")
                && headers.getFirstNativeHeader("room").equalsIgnoreCase("disconnect")
                && roomId != -1) {
            rooms.get(roomId).onPlayerDisconnect(headers.getUser().getName(), true).ifPresent(
                    playerInfoDto -> sendRoomEvent(roomId, playerInfoDto)
            );
            playerRoomIdMap.remove(headers.getSessionId());
        }
    }

    @Transactional(readOnly = true)
    RoomInfoDto convertRoomIntoDto(Room room) {
        return new RoomInfoDto(
                room.getId(),
                room.getName(),
                room.getMaxPlayers(),
                room.getCurrentPlayers(),
                room.isLocked(),
                room.isTournament(),
                room.isScenario(),
                room.getScenario(),
                room.getPlayers()
                        .values()
                        .stream()
                        .map((player) -> player.getUser().getUserName())
                        .collect(Collectors.toList()),
                room.getRequirement(),
                room.getState(),
                room.getMaxRounds(),
                room.getCurrentRound()
        );
    }

}
