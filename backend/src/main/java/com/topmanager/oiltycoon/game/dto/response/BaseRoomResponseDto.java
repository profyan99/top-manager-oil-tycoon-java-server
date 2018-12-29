package com.topmanager.oiltycoon.game.dto.response;

public abstract class BaseRoomResponseDto {
    protected ResponseDtoType type;

    public BaseRoomResponseDto(ResponseDtoType type) {
        this.type = type;
    }
}
