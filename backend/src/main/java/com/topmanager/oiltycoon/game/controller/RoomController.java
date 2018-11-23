package com.topmanager.oiltycoon.game.controller;

import com.topmanager.oiltycoon.game.dto.response.RoomInfoDto;
import com.topmanager.oiltycoon.game.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class RoomController {

    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);

    private RoomService roomService;

    @Autowired
    public void setRoomService(RoomService roomService) {
        this.roomService = roomService;
    }

    @MessageMapping("/roomList")
    @SendToUser("/queue/roomList")
    public List<RoomInfoDto> room() {
        return roomService.getRoomsList();
    }
}
