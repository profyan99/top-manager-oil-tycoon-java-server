package com.topmanager.oiltycoon.game.dto.request;

public class RoomChatMessageRequestDto {
    private int roomId;
    private String message;

    public RoomChatMessageRequestDto(int roomId, String message) {
        this.roomId = roomId;
        this.message = message;
    }

    public RoomChatMessageRequestDto(String message) {
        this.message = message;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
