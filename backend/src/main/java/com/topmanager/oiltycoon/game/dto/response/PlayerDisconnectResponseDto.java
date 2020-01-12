package com.topmanager.oiltycoon.game.dto.response;

import com.topmanager.oiltycoon.game.model.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PlayerDisconnectResponseDto extends PlayerInfoResponseDto {

    public PlayerDisconnectResponseDto(PlayerDisconnectDto playerDisconnectDto) {
        super(ResponseEventType.DISCONNECT, playerDisconnectDto);
    }

    @NoArgsConstructor
    @Getter
    public static class PlayerDisconnectDto extends PlayerInfoResponseDto.PlayerInfoDto {
        private boolean force;

        public PlayerDisconnectDto(Player player, boolean force) {
            super(player);
            this.force = force;
        }
    }
}
