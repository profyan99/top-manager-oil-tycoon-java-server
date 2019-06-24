package com.topmanager.oiltycoon.game.dto.response;

import com.topmanager.oiltycoon.game.model.Player;

public class PlayerConnectDto extends PlayerInfoResponseDto {
    public PlayerConnectDto(Player player) {
        super(ResponseEventType.CONNECT, new PlayerInfoResponseDto.PlayerInfoDto(player));
    }
}
