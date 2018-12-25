package com.topmanager.oiltycoon.game.dto.response;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class PlayerInfoDto extends BaseRoomResponseDto {
    //TODO complete necessary user information


    public PlayerInfoDto() {
        super(ResponseDtoType.PLAYER_INFO);
    }
}
