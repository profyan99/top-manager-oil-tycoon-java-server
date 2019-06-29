package com.topmanager.oiltycoon.game.service.impl;

import com.topmanager.oiltycoon.game.dao.RoomDao;
import com.topmanager.oiltycoon.game.dto.response.ResponseEventType;
import com.topmanager.oiltycoon.game.dto.response.RoomInfoAddResponseDto;
import com.topmanager.oiltycoon.game.dto.response.RoomInfoResponseDto;
import com.topmanager.oiltycoon.game.model.Room;
import com.topmanager.oiltycoon.game.service.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class RoomListService {

    private final RoomDao roomDao;
    private final MessageSender messageSender;

    @Autowired
    public RoomListService(RoomDao roomDao, MessageSender messageSender) {
        this.roomDao = roomDao;
        this.messageSender = messageSender;
    }

    @Transactional(readOnly = true)
    public RoomInfoAddResponseDto getRoomsList() {
        return new RoomInfoAddResponseDto(
                ResponseEventType.ADD,
                roomDao.findAll()
                        .stream()
                        .map(RoomInfoResponseDto.RoomInfoDto::new)
                        .collect(Collectors.toList())
        );
    }

    @Transactional
    public void addRoom(Room room) {
        roomDao.save(room);
        messageSender.sendToRoomListDest(new RoomInfoAddResponseDto(
                ResponseEventType.ADD,
                Collections.singletonList(new RoomInfoResponseDto.RoomInfoDto(room))
        ));
    }

    @Transactional
    public void updateRoom(Room room) {
        roomDao.save(room);
        messageSender.sendToRoomListDest(new RoomInfoResponseDto(
                ResponseEventType.UPDATE,
                new RoomInfoResponseDto.RoomInfoDto(room)
        ));
    }

    @Transactional
    public void deleteRoom(Room room) {
        roomDao.delete(room);
        messageSender.sendToRoomListDest(new RoomInfoResponseDto(
                ResponseEventType.REMOVE,
                new RoomInfoResponseDto.RoomInfoDto(room)
        ));
    }
}
