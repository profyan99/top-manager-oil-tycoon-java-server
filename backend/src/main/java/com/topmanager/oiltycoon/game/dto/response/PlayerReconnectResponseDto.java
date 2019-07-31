package com.topmanager.oiltycoon.game.dto.response;

import com.topmanager.oiltycoon.game.dto.CompanyDto;

public class PlayerReconnectResponseDto extends PlayerInfoResponseDto {
    public PlayerReconnectResponseDto(String userName, String avatar, int id, CompanyDto company) {
        super(ResponseEventType.RECONNECT, new PlayerInfoResponseDto.PlayerInfoDto(userName, avatar, id, company));
    }
}
