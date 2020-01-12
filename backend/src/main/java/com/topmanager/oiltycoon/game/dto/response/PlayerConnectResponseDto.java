package com.topmanager.oiltycoon.game.dto.response;

import com.topmanager.oiltycoon.game.model.Player;

public class PlayerConnectResponseDto extends PlayerInfoResponseDto {
    public PlayerConnectResponseDto(Player player) {
        super(ResponseEventType.CONNECT, new PlayerInfoResponseDto.PlayerInfoDto(player));
    }
}
