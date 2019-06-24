package com.topmanager.oiltycoon.game.dto.response;

import com.topmanager.oiltycoon.game.model.Player;

public class PlayerDisconnectDto extends PlayerInfoResponseDto {
    public PlayerDisconnectDto(Player player) {
        super(ResponseEventType.DISCONNECT, new PlayerInfoResponseDto.PlayerInfoDto(player));
    }
}
