package com.topmanager.oiltycoon.game.dto.request;

import com.topmanager.oiltycoon.game.model.Requirement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class RoomAddDto {
    private String name;
    private int maxPlayers;
    private boolean isLocked;
    private boolean isTournament;
    private boolean isScenario;
    private String scenario;
    private Requirement requirement;
    private int maxRounds;
    private String password;
    private int roomPeriodDelay;
}
