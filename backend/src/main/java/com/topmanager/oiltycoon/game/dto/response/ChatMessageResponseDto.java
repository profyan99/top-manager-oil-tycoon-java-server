package com.topmanager.oiltycoon.game.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ChatMessageResponseDto extends BaseRoomResponseDto<ChatMessageResponseDto.ChatMessageDto> {

    public ChatMessageResponseDto(ChatMessageDto body) {
        super(ResponseObjectType.MESSAGE, ResponseEventType.ADD, body);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class ChatMessageDto {
        private String message;
        private UserInfoResponseDto.UserInfoDto player;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime time;
    }
}
