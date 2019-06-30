package com.topmanager.oiltycoon.game.dto.response;

public class PlayerReconnectResponseDto extends PlayerInfoResponseDto {
    public PlayerReconnectResponseDto(String userName, String avatar, int id) {
        super(ResponseEventType.CONNECT, new PlayerInfoResponseDto.PlayerInfoDto(userName, avatar, id));
    }
}
