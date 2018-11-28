package com.topmanager.oiltycoon.game.model;

import com.topmanager.oiltycoon.social.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private UserDto user;
    private long timeEndReload;
    private boolean connected;

    public Player(UserDto user) {
        this.user = user;
    }
}
