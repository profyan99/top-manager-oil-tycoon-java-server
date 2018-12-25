package com.topmanager.oiltycoon.game.dto.response;

import lombok.*;

@ToString
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class GameInfoDto extends BaseRoomResponseDto{
    //TODO Gamedto complete


    public GameInfoDto() {
        super(ResponseDtoType.GAME_INFO);
    }
}
