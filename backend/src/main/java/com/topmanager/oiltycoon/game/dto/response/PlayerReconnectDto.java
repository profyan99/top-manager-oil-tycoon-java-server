package com.topmanager.oiltycoon.game.dto.response;

public class PlayerReconnectDto extends PlayerInfoResponseDto {
    public PlayerReconnectDto(String userName, String avatar, int id) {
        super(ResponseEventType.CONNECT, new PlayerInfoResponseDto.PlayerInfoDto(userName, avatar, id));
    }
}
