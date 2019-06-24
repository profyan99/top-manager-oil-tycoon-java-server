package com.topmanager.oiltycoon.game.dto.response;

public class ServerMessageResponseDto extends BaseRoomResponseDto<ServerMessageResponseDto.ServerMessageDto> {

    public ServerMessageResponseDto(ResponseEventType eventType, ServerMessageDto body) {
        super(ResponseObjectType.SERVER, eventType, body);
    }

    public static class ServerMessageDto {
        private String message;

        public ServerMessageDto(String reason) {
            this.message = reason;
        }

        public String getReason() {
            return message;
        }
    }

}
