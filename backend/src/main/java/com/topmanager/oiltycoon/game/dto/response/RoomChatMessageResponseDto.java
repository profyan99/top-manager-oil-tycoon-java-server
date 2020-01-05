package com.topmanager.oiltycoon.game.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

public class RoomChatMessageResponseDto extends BaseRoomResponseDto<RoomChatMessageResponseDto.RoomChatMessageDto> {

    public RoomChatMessageResponseDto(RoomChatMessageDto body) {
        super(ResponseObjectType.MESSAGE, ResponseEventType.ADD, body);
    }

    public static class RoomChatMessageDto {
        private String message;
        private PlayerInfoResponseDto.PlayerInfoDto player;

        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime time;

        public RoomChatMessageDto(String message, PlayerInfoResponseDto.PlayerInfoDto player, LocalTime time) {
            this.message = message;
            this.player = player;
            this.time = time;
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

        public LocalTime getDate() {
            return time;
        }

        public void setDate(LocalTime time) {
            this.time = time;
        }
    }
}
