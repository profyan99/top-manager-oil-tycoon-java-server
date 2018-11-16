package com.topmanager.oiltycoon.game.controller;

import com.topmanager.oiltycoon.RestExceptionHandler;
import com.topmanager.oiltycoon.game.dto.BaseMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Controller;

@Controller
public class RoomController {

    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);

    @MessageMapping("/room")
    @SendTo("/topic/room")
    public BaseMessageDto room(BaseMessageDto message) {
        logger.error("Input message: "+message.getMessage());
        return new BaseMessageDto("Hello world");
    }
}
