package com.topmanager.oiltycoon.game.dto.response;


import com.topmanager.oiltycoon.game.model.Player;
import com.topmanager.oiltycoon.game.model.PlayerState;
import com.topmanager.oiltycoon.game.model.game.company.CompanyStatistics;
import com.topmanager.oiltycoon.social.model.User;
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
    public static class PlayerInfoDto extends UserInfoResponseDto.UserInfoDto {
        private String companyName;
        private PlayerState state;
        private CompanyStatistics statistics;

        public PlayerInfoDto(User user, String companyName, PlayerState state, CompanyStatistics statistics) {
            super(user);
            this.companyName = companyName;
            this.state = state;
            this.statistics = statistics;
        }

        public PlayerInfoDto(Player player) {
            this(
                    player.getUser(),
                    player.getCompany().getName(),
                    player.getState(),
                    player.getCompany().getStatistics()
            );
        }
    }

}
