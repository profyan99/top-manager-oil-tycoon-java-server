package com.topmanager.oiltycoon.game.dto.response;

import com.topmanager.oiltycoon.game.model.Player;

public class PlayerReconnectResponseDto extends PlayerInfoResponseDto {
    public PlayerReconnectResponseDto(Player player) {
        super(ResponseEventType.RECONNECT, new PlayerInfoResponseDto.PlayerInfoDto(player));
    }
}
