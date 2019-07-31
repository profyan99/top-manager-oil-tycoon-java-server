package com.topmanager.oiltycoon.game.dto.response;


import com.topmanager.oiltycoon.game.dto.CompanyDto;

public class PlayerInfoResponseDto extends BaseRoomResponseDto<PlayerInfoResponseDto.PlayerInfoDto> {

    public PlayerInfoResponseDto(ResponseEventType eventType, PlayerInfoDto body) {
        super(ResponseObjectType.PLAYER, eventType, body);
    }

    public static class PlayerInfoDto {
        private String userName;
        private String avatar;
        private int id;
        private CompanyDto company;

        public PlayerInfoDto(String userName, String avatar, int id, CompanyDto company) {
            this.userName = userName;
            this.avatar = avatar;
            this.id = id;
            this.company = company;
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

        public CompanyDto getCompany() {
            return company;
        }

        public void setCompany(CompanyDto company) {
            this.company = company;
        }
    }

}
