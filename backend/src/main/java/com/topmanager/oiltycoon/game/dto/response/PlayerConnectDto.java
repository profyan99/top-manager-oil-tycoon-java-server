package com.topmanager.oiltycoon.game.dto.response;

import com.topmanager.oiltycoon.game.model.Player;

public class PlayerConnectDto extends PlayerInfoDto {
    public PlayerConnectDto(Player player) {
        super(player, ResponseDtoType.PLAYER_CONNECT);
    }
}
