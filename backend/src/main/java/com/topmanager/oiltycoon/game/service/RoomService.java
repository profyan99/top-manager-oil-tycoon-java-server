package com.topmanager.oiltycoon.game.service;

import com.topmanager.oiltycoon.game.dao.RoomDao;
import com.topmanager.oiltycoon.game.dto.request.RoomAddDto;
import com.topmanager.oiltycoon.game.dto.request.RoomConnectDto;
import com.topmanager.oiltycoon.game.dto.response.GameInfoDto;
import com.topmanager.oiltycoon.game.dto.response.PlayerInfoDto;
import com.topmanager.oiltycoon.game.dto.response.RoomInfoDto;
import com.topmanager.oiltycoon.game.model.Player;
import com.topmanager.oiltycoon.game.model.Room;
import com.topmanager.oiltycoon.game.service.impl.RoomProcessor;
import com.topmanager.oiltycoon.social.model.GameStats;
import com.topmanager.oiltycoon.social.model.User;
import com.topmanager.oiltycoon.social.security.exception.ErrorCode;
import com.topmanager.oiltycoon.social.security.exception.RestException;
import com.topmanager.oiltycoon.social.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
public class RoomService {
    private Map<Integer, RoomProcessor> rooms;
    private RoomDao roomDao;
    private SimpMessagingTemplate messagingTemplate;
    private RoomSchedulingService roomSchedulingService;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);

    public RoomService() {
        rooms = new HashMap<>();
    }

//    @Autowired
//    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
//        this.passwordEncoder = passwordEncoder;
//    }

    @Autowired
    public void setRoomDao(RoomDao roomDao) {
        this.roomDao = roomDao;
    }

    @Autowired
    @Lazy
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRoomSchedulingService(RoomSchedulingService roomSchedulingService) {
        this.roomSchedulingService = roomSchedulingService;
    }

    @Autowired
    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @PostConstruct
    public void init() {
        rooms.putAll(
                StreamSupport.stream(roomDao.findAll().spliterator(), false)
                .map((room) -> new RoomProcessor(new RoomProcessor.RoomProcessorParams(room, this, passwordEncoder)))
                .peek(roomProcessor -> roomSchedulingService.addRoomRunnable(roomProcessor))
                .collect(Collectors.toMap(
                        roomProcessor -> roomProcessor.getRoomData().getId(),
                        roomProcessor -> roomProcessor)
                )
        );

    }

    public List<RoomInfoDto> getRoomsList() {
        return rooms.values()
                .stream()
                .map(roomProcessor -> convertRoomIntoDto(roomProcessor.getRoomData()))
                .collect(Collectors.toList());
    }

    public void addRoom(RoomAddDto roomAdd) {
        if (rooms
                .values()
                .stream()
                .anyMatch((roomProcessor -> roomProcessor.getRoomData().getName().equals(roomAdd.getName())))) {
            throw new RestException(ROOM_NAME_NOT_UNIQUE);
        }
        Room room = new Room(
                roomAdd.getName(),
                roomAdd.getMaxPlayers(),
                roomAdd.isLocked(),
                roomAdd.isTournament(),
                roomAdd.isScenario(),
                roomAdd.getScenario(),
                roomAdd.getRequirement(),
                roomAdd.getMaxRounds(),
                passwordEncoder.encode(roomAdd.getPassword()),
                roomAdd.getRoomPeriodDelay()
        );
        room = roomDao.save(room);
        RoomProcessor roomProcessor = new RoomProcessor(new RoomProcessor.RoomProcessorParams(room, this, passwordEncoder));
        rooms.put(room.getId(), roomProcessor);
        roomSchedulingService.addRoomRunnable(roomProcessor);
        updateRoomList();
    }

    public void deleteRoom(int roomId) {
        Room deletedRoom = rooms.get(roomId).getRoomData();
        if (deletedRoom == null) {
            deletedRoom = roomDao.findById(roomId).orElseThrow(
                    () -> new RestException(ROOM_NOT_FOUND)
            );
        }
        roomSchedulingService.removeRoomRunnable(rooms.get(roomId));
        roomDao.delete(deletedRoom);
        //TODO stop and handle users
        rooms.remove(roomId);
        updateRoomList();
    }

    public GameInfoDto connectToRoom(RoomConnectDto roomConnectDto) {
        User user = userService.getUser();
        RoomProcessor currentRoom = rooms.get(roomConnectDto.getRoomId());
        if(currentRoom == null) {
            throw new RestException(ErrorCode.INVALID_ROOM_ID);
        }
        currentRoom.onPlayerConnect(
                new Player(user),
                roomConnectDto.getPassword()
        );
        return new GameInfoDto();
    }

    private void updateRoomList() {
        messagingTemplate.convertAndSend(BROKER_DESTINATION_PREFIX + ROOM_LIST,
                rooms
                        .values()
                        .stream()
                        .map(roomProcessor -> convertRoomIntoDto(roomProcessor.getRoomData()))
                        .collect(Collectors.toList())
        );
    }

    public void updateUserGameStats(GameStats gameStats) {
        userService.updateGameStats(gameStats);
    }

    public void sendUserConnect(int roomId, PlayerInfoDto playerInfoDto) {
        messagingTemplate.convertAndSend(
                BROKER_DESTINATION_PREFIX +ROOM_EVENT+ "/" + roomId,
                playerInfoDto
        );
    }

    public void sendUserDisconnect(int roomId, PlayerInfoDto playerInfoDto) {

        messagingTemplate.convertAndSend(
                BROKER_DESTINATION_PREFIX + ROOM_EVENT + "/" + roomId,
                playerInfoDto
        );
    }

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());

        //messagingTemplate.convertAndSendToUser(headers.getUser().getName(),);
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        int roomId = (int)headerAccessor.getSessionAttributes().getOrDefault("CONNECTED_ROOM", -1);
        logger.debug(":: Session disconnect\nroomId: "+roomId+"\nname: "+headerAccessor.getUser().getName());
        if(roomId != -1) {
            rooms.get(roomId).onPlayerDisconnect(headerAccessor.getUser().getName());
        }
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {

    }

    @EventListener
    public void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {

    }

    private RoomInfoDto convertRoomIntoDto(Room room) {
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
