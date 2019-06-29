package com.topmanager.oiltycoon.game.dto.response;


public class PlayerInfoResponseDto extends BaseRoomResponseDto<PlayerInfoResponseDto.PlayerInfoDto> {

    public PlayerInfoResponseDto(ResponseEventType eventType, PlayerInfoDto body) {
        super(ResponseObjectType.PLAYER, eventType, body);
    }

    public static class PlayerInfoDto {
        private String userName;
        private String avatar;
        private int id;

        public PlayerInfoDto(String userName, String avatar, int id) {
            this.userName = userName;
            this.avatar = avatar;
            this.id = id;
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
