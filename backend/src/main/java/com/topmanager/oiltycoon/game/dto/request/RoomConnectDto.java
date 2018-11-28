package com.topmanager.oiltycoon.game.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class RoomConnectDto {
    private int roomId;
    private String password;
}
