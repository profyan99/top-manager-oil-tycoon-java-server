package com.topmanager.oiltycoon.game.service;

import com.topmanager.oiltycoon.game.dto.response.BaseRoomResponseDto;

public interface RoomEventHandler {
    void deleteRoom(int roomId);

    void sendRoomEvent(int roomId, BaseRoomResponseDto responseDto);

    void updateRoom(int roomId);
}
