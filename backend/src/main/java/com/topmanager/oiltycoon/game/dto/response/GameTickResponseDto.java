package com.topmanager.oiltycoon.game.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class GameTickResponseDto extends BaseRoomResponseDto<GameTickResponseDto.GameTickDto> {

    public GameTickResponseDto(GameTickDto body) {
        super(ResponseObjectType.GAME, ResponseEventType.TICK, body);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public static class GameTickDto {
        private int amount;
    }
}
