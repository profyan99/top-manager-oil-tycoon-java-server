package com.topmanager.oiltycoon.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.topmanager.oiltycoon.game.model.GameState.PREPARE;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    private int id;
    private String name;
    private int maxPlayers;
    private int currentPlayers;
    private boolean isLocked;
    private boolean isTournament;
    private boolean isScenario;
    private String scenario;
    private Map<Integer, Player> players;
    private Requirement requirement;
    private GameState state;
    private int maxRounds;
    private int currentRound;

    public Room(String name, int maxPlayers, boolean isLocked, boolean isTournament, boolean isScenario, String scenario,
                Requirement requirement, int maxRounds) {
        this(0, name, maxPlayers, 0, isLocked, isTournament, isScenario, scenario,
                new HashMap<>(), requirement, PREPARE, maxRounds, 0);
    }
}
