package com.topmanager.oiltycoon.game.dto.response;

import com.topmanager.oiltycoon.game.dto.CompanyDto;

public class PlayerConnectResponseDto extends PlayerInfoResponseDto {
    public PlayerConnectResponseDto(String userName, String avatar, int id, CompanyDto company) {
        super(ResponseEventType.CONNECT, new PlayerInfoResponseDto.PlayerInfoDto(userName, avatar, id, company));
    }
}
