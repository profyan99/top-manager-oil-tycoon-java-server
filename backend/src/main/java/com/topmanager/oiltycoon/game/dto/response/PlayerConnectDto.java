package com.topmanager.oiltycoon.game.dto.response;

public class PlayerConnectDto extends PlayerInfoResponseDto {
    public PlayerConnectDto(String userName, String avatar, int id) {
        super(ResponseEventType.CONNECT, new PlayerInfoResponseDto.PlayerInfoDto(userName, avatar, id));
    }
}
