package com.topmanager.oiltycoon.game.dto.response;


import com.topmanager.oiltycoon.game.model.Player;

public class PlayerInfoResponseDto extends BaseRoomResponseDto<PlayerInfoResponseDto.PlayerInfoDto> {

    public PlayerInfoResponseDto(ResponseEventType eventType, PlayerInfoDto body) {
        super(ResponseObjectType.PLAYER, eventType, body);
    }

    public static class PlayerInfoDto {
        private String userName;
        private String avatar;
        private int id;

        public PlayerInfoDto(Player player) {
            this.userName = player.getUserName();
            this.avatar = player.getUser().getAvatar();
            this.id = player.getUser().getId();
        }

        public PlayerInfoDto() {
        }

        public String getUserName() {
            return userName;
        }

        public String getAvatar() {
            return avatar;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

}
