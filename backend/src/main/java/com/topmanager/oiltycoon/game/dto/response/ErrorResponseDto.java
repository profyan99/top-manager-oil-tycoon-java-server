package com.topmanager.oiltycoon.game.dto.response;

import com.topmanager.oiltycoon.social.dto.ErrorDto;

public class ErrorResponseDto extends BaseRoomResponseDto<ErrorDto> {
    public ErrorResponseDto(ErrorDto body) {
        super(ResponseObjectType.ERROR, ResponseEventType.NONE, body);
    }
}
