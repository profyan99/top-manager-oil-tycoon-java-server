package com.topmanager.oiltycoon.game.dto.response;


import com.topmanager.oiltycoon.game.model.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class PlayerInfoResponseDto extends BaseRoomResponseDto<PlayerInfoResponseDto.PlayerInfoDto> {

    public PlayerInfoResponseDto(ResponseEventType eventType, PlayerInfoDto body) {
        super(ResponseObjectType.PLAYER, eventType, body);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class PlayerInfoDto {
        private String userName;
        private String avatar;
        private String companyName;
        private int id;

        public PlayerInfoDto(Player player) {
            this(
                    player.getUserName(),
                    player.getUser().getAvatar(),
                    player.getCompany().getName(),
                    player.getUser().getId()
            );
        }
    }

}
