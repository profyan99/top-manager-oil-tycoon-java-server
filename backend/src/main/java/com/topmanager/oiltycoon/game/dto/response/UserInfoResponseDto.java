package com.topmanager.oiltycoon.game.dto.response;

import com.topmanager.oiltycoon.social.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserInfoResponseDto extends BaseRoomResponseDto<UserInfoResponseDto.UserInfoDto> {

    public UserInfoResponseDto(ResponseEventType eventType, UserInfoDto body) {
        super(ResponseObjectType.PLAYER, eventType, body);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class UserInfoDto {
        private int id;
        private String userName;
        private String avatar;

        public UserInfoDto(User user) {
            this(user.getId(), user.getUserName(), user.getAvatar());
        }
    }
}
