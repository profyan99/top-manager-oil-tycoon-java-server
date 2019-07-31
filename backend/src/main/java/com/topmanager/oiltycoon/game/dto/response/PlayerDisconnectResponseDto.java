package com.topmanager.oiltycoon.game.dto.response;

import com.topmanager.oiltycoon.game.dto.CompanyDto;

public class PlayerDisconnectResponseDto extends PlayerInfoResponseDto {

    public PlayerDisconnectResponseDto(PlayerDisconnectDto playerDisconnectDto) {
        super(ResponseEventType.DISCONNECT, playerDisconnectDto);
    }

    public static class PlayerDisconnectDto extends PlayerInfoResponseDto.PlayerInfoDto {
        private boolean force;

        public PlayerDisconnectDto(String userName, String avatar, int id, CompanyDto company, boolean force) {
            super(userName, avatar, id, company);
            this.force = force;
        }

        public PlayerDisconnectDto() {
        }

        public boolean isForce() {
            return force;
        }
    }
}
