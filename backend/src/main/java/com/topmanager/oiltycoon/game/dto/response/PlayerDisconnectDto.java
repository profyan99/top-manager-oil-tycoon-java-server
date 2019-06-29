package com.topmanager.oiltycoon.game.dto.response;

public class PlayerDisconnectDto extends PlayerInfoResponseDto {

    public PlayerDisconnectDto(String userName, String avatar, int id) {
        super(ResponseEventType.DISCONNECT, new PlayerInfoResponseDto.PlayerInfoDto(userName, avatar, id));
    }
}
