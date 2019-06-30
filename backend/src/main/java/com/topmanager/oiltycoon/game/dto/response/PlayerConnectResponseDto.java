package com.topmanager.oiltycoon.game.dto.response;

public class PlayerConnectResponseDto extends PlayerInfoResponseDto {
    public PlayerConnectResponseDto(String userName, String avatar, int id) {
        super(ResponseEventType.CONNECT, new PlayerInfoResponseDto.PlayerInfoDto(userName, avatar, id));
    }
}
