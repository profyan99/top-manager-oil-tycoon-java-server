package com.topmanager.oiltycoon.game.dto.response;

public class RoomChatMessageResponseDto extends BaseRoomResponseDto<RoomChatMessageResponseDto.RoomChatMessageDto> {

    public RoomChatMessageResponseDto(RoomChatMessageDto body) {
        super(ResponseObjectType.MESSAGE, ResponseEventType.ADD, body);
    }

    public static class RoomChatMessageDto {
        private String message;
        private PlayerInfoResponseDto.PlayerInfoDto player;

        public RoomChatMessageDto(String message, PlayerInfoResponseDto.PlayerInfoDto player) {
            this.message = message;
            this.player = player;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public PlayerInfoResponseDto.PlayerInfoDto getPlayer() {
            return player;
        }

        public void setPlayer(PlayerInfoResponseDto.PlayerInfoDto player) {
            this.player = player;
        }
    }
}
