package com.topmanager.oiltycoon.game.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RoomChatMessageRequestDto extends ChatMessageRequestDto {
    private int roomId;
}
