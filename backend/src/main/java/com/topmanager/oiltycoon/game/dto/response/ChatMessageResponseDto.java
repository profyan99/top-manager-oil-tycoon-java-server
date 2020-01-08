package com.topmanager.oiltycoon.game.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class ChatMessageResponseDto extends BaseRoomResponseDto<ChatMessageResponseDto.ChatMessageDto> {

    public ChatMessageResponseDto(ChatMessageDto body) {
        super(ResponseObjectType.MESSAGE, ResponseEventType.ADD, body);
    }

    public static class ChatMessageDto {
        private String message;
        private PlayerInfoResponseDto.PlayerInfoDto player;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime time;

        public ChatMessageDto(String message, PlayerInfoResponseDto.PlayerInfoDto player, LocalDateTime time) {
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

        public LocalDateTime getDate() {
            return time;
        }

        public void setDate(LocalDateTime time) {
            this.time = time;
        }
    }
}
