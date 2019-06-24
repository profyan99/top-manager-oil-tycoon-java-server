package com.topmanager.oiltycoon.game.dto.response;

import java.util.List;

public class RoomInfoAddResponseDto extends BaseRoomResponseDto<List<RoomInfoResponseDto.RoomInfoDto>> {
    public RoomInfoAddResponseDto(ResponseEventType eventType,
                                  List<RoomInfoResponseDto.RoomInfoDto> body) {
        super(ResponseObjectType.ROOM_PREVIEW, eventType, body);
    }
}
