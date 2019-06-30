package com.topmanager.oiltycoon.game.dto.response;

import java.util.List;

public class RoomInfoAddResponseDto extends BaseRoomResponseDto<List<RoomInfoResponseDto.RoomInfoDto>> {
    public RoomInfoAddResponseDto(List<RoomInfoResponseDto.RoomInfoDto> body) {
        super(ResponseObjectType.ROOM_PREVIEW, ResponseEventType.ADD, body);
    }
}
