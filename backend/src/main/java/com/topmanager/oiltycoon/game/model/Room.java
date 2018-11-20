package com.topmanager.oiltycoon.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private List<Player> players;
    private Requirement requirement;
    private GameState state;
    private int maxRounds;
    private int currentRound;
}
