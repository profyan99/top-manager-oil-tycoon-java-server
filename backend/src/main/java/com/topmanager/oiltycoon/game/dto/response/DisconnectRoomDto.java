package com.topmanager.oiltycoon.game.dto.response;

public class DisconnectRoomDto extends BaseRoomResponseDto {
    private String reason;

    public DisconnectRoomDto(String reason) {
        super(ResponseDtoType.DISCONNECT);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
