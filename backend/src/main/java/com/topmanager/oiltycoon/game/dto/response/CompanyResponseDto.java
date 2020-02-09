package com.topmanager.oiltycoon.game.dto.response;

import com.topmanager.oiltycoon.game.dto.CompanyDto;

public class CompanyResponseDto extends BaseRoomResponseDto<CompanyDto> {
    public CompanyResponseDto(ResponseEventType eventType, CompanyDto body) {
        super(ResponseObjectType.COMPANY, eventType, body);
    }
}
