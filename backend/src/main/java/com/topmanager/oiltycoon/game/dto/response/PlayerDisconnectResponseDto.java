package com.topmanager.oiltycoon.game.dto.response;

public class PlayerDisconnectResponseDto extends PlayerInfoResponseDto {

    public PlayerDisconnectResponseDto(PlayerDisconnectDto playerDisconnectDto) {
        super(ResponseEventType.DISCONNECT, playerDisconnectDto);
    }

    public static class PlayerDisconnectDto extends PlayerInfoResponseDto.PlayerInfoDto {
        private boolean force;

        public PlayerDisconnectDto(String userName, String avatar, int id, boolean force) {
            super(userName, avatar, id);
            this.force = force;
        }

        public PlayerDisconnectDto() {
        }

        public boolean isForce() {
            return force;
        }
    }
}
