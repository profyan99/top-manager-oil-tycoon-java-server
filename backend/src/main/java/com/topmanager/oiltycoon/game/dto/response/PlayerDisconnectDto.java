package com.topmanager.oiltycoon.game.dto.response;

import com.topmanager.oiltycoon.game.model.Player;

public class PlayerDisconnectDto extends PlayerInfoDto {
    public PlayerDisconnectDto(Player player) {
        super(player, ResponseDtoType.PLAYER_DISCONNECT);
    }
}
