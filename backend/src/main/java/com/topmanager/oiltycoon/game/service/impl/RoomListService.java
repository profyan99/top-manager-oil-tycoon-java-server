package com.topmanager.oiltycoon.game.service.impl;

import com.topmanager.oiltycoon.game.dao.RoomDao;
import com.topmanager.oiltycoon.game.dto.request.ChatMessageRequestDto;
import com.topmanager.oiltycoon.game.dto.response.*;
import com.topmanager.oiltycoon.game.model.Room;
import com.topmanager.oiltycoon.game.service.MessageSender;
import com.topmanager.oiltycoon.social.model.User;
import com.topmanager.oiltycoon.social.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class RoomListService {

    private final RoomDao roomDao;
    private final MessageSender messageSender;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(RoomListService.class);

    @Autowired
    public RoomListService(RoomDao roomDao, MessageSender messageSender, UserService userService) {
        this.roomDao = roomDao;
        this.messageSender = messageSender;
        this.userService = userService;
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
        messageSender.sendToRoomListDest(new RoomInfoAddResponseDto(
                Collections.singletonList(new RoomInfoResponseDto.RoomInfoDto(room))
        ));
    }

    @Transactional
    public void updateRoom(Room room) {
        messageSender.sendToRoomListDest(new RoomInfoResponseDto(
                ResponseEventType.UPDATE,
                new RoomInfoResponseDto.RoomInfoDto(room)
        ));
    }

    @Transactional
    public void deleteRoom(Room room) {
        messageSender.sendToRoomListDest(new RoomInfoResponseDto(
                ResponseEventType.REMOVE,
                new RoomInfoResponseDto.RoomInfoDto(room)
        ));
    }

    public void sendChatMessage(ChatMessageRequestDto requestDto) {
        User currentUser = userService.getUser();
        messageSender.sendToRoomListDest(new ChatMessageResponseDto(
                new ChatMessageResponseDto.ChatMessageDto(
                        requestDto.getMessage(),
                        new UserInfoResponseDto.UserInfoDto(currentUser),
                        LocalDateTime.now())
        ));
    }
}
