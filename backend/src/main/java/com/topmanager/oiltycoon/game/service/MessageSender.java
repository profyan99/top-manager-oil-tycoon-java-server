package com.topmanager.oiltycoon.game.service;

public interface MessageSender {
    void sendToUserDest(String user, Object payload);

    void sendToRoomDest(int roomId, Object payload);

    void sendToRoomListDest(Object payload);
}
