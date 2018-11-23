package com.topmanager.oiltycoon.game.service;

import com.topmanager.oiltycoon.game.dao.RoomDao;
import com.topmanager.oiltycoon.game.dto.request.RoomAddDto;
import com.topmanager.oiltycoon.game.dto.response.RoomInfoDto;
import com.topmanager.oiltycoon.game.model.Room;
import com.topmanager.oiltycoon.social.security.exception.RestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.context.event.EventListener;

import static com.topmanager.oiltycoon.game.model.RoomDestination.ADD_ROOM;
import static com.topmanager.oiltycoon.social.security.exception.ErrorCode.ROOM_NAME_NOT_UNIQUE;

@Service
public class RoomService {
    private Map<Integer, Room> rooms;

    private RoomDao roomDao;

    private SimpMessagingTemplate messagingTemplate;

    public RoomService() {
        rooms = new HashMap<>();
    }

    @Autowired
    public void setRoomDao(RoomDao roomDao) {
        this.roomDao = roomDao;
    }

    @Autowired
    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public List<RoomInfoDto> getRoomsList() {
        return roomDao.findAllRooms()
                .stream()
                .map(this::convertRoomIntoDto)
                .collect(Collectors.toList());
    }

    public void addRoom(RoomAddDto roomAdd) {
        if(roomDao.existsByName(roomAdd.getName())) {
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
                roomAdd.getMaxRounds()
        );
        roomDao.addRoom(room);
        rooms.put(room.getId(), room);
        messagingTemplate.convertAndSend(ADD_ROOM.getDestination(), convertRoomIntoDto(room));
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

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        //messagingTemplate.convertAndSendToUser(headers.getUser().getName(),);
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {

    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
    }

    @EventListener
    public void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {

    }
}
