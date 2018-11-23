package com.topmanager.oiltycoon.game.dto.response;

import com.topmanager.oiltycoon.game.model.GameState;
import com.topmanager.oiltycoon.game.model.Player;
import com.topmanager.oiltycoon.game.model.Requirement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@ToString
public class RoomInfoDto {
    private int id;
    private String name;
    private int maxPlayers;
    private int currentPlayers;
    private boolean isLocked;
    private boolean isTournament;
    private boolean isScenario;
    private String scenario;
    private List<String> players;
    private Requirement requirement;
    private GameState state;
    private int maxRounds;
    private int currentRound;
}
