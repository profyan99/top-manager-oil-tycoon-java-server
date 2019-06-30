package com.topmanager.oiltycoon.game.service.impl;

import com.topmanager.oiltycoon.game.service.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageSenderImpl implements MessageSender {

    @Value("${ws.destination.user}")
    private String userBaseDestination;

    @Value("${ws.destination.room}")
    private String roomBaseDestination;

    @Value("${ws.destination.room-list}")
    private String roomListDestination;

    private SimpMessagingTemplate messagingTemplate;
    private static final Logger logger = LoggerFactory.getLogger(MessageSenderImpl.class);

    @Autowired
    public MessageSenderImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void sendToUserDest(String user, Object payload) {
        messagingTemplate.convertAndSendToUser(
                user,
                userBaseDestination,
                payload
        );
        logger.debug("MessageSender :: sendToUser[{}]: {}", userBaseDestination, user);
    }

    @Override
    public void sendToRoomDest(int roomId, Object payload) {
        messagingTemplate.convertAndSend(
                roomBaseDestination + roomId,
                payload
        );
        logger.debug("MessageSender :: sendToRoom[{}]: {}", roomBaseDestination, roomId);
    }

    @Override
    public void sendToRoomListDest(Object payload) {
        messagingTemplate.convertAndSend(
                roomListDestination,
                payload
        );
        logger.debug("MessageSender :: sendToRoomList[{}]", roomListDestination);
    }
}
