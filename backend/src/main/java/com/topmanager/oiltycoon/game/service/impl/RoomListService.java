package com.topmanager.oiltycoon.game.service.impl;

import com.topmanager.oiltycoon.game.dao.RoomDao;
import com.topmanager.oiltycoon.game.dto.response.ResponseEventType;
import com.topmanager.oiltycoon.game.dto.response.RoomInfoAddResponseDto;
import com.topmanager.oiltycoon.game.dto.response.RoomInfoResponseDto;
import com.topmanager.oiltycoon.game.model.Room;
import com.topmanager.oiltycoon.game.service.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class RoomListService {

    private final RoomDao roomDao;
    private final MessageSender messageSender;
    private static final Logger logger = LoggerFactory.getLogger(RoomListService.class);

    @Autowired
    public RoomListService(RoomDao roomDao, MessageSender messageSender) {
        this.roomDao = roomDao;
        this.messageSender = messageSender;
    }

    @Transactional(readOnly = true)
    public RoomInfoAddResponseDto getRoomsList() {
        logger.debug("Room list :: " + "get");
        return new RoomInfoAddResponseDto(
                roomDao.findAll()
                        .stream()
                        .map(RoomInfoResponseDto.RoomInfoDto::new)
                        .collect(Collectors.toList())
        );
    }

    @Transactional
    public void addRoom(Room room) {
        logger.debug("Room [" + room.getName() + "] :: " + "add");
        roomDao.save(room);
        messageSender.sendToRoomListDest(new RoomInfoAddResponseDto(
                Collections.singletonList(new RoomInfoResponseDto.RoomInfoDto(room))
        ));
    }

    @Transactional
    public void updateRoom(Room room) {
        logger.debug("Room [" + room.getName() + "] :: " + "update");
        roomDao.save(room);
        messageSender.sendToRoomListDest(new RoomInfoResponseDto(
                ResponseEventType.UPDATE,
                new RoomInfoResponseDto.RoomInfoDto(room)
        ));
    }

    @Transactional
    public void deleteRoom(Room room) {
        logger.debug("Room [" + room.getName() + "] :: " + "delete");
        roomDao.delete(room);
        messageSender.sendToRoomListDest(new RoomInfoResponseDto(
                ResponseEventType.REMOVE,
                new RoomInfoResponseDto.RoomInfoDto(room)
        ));
    }
}
