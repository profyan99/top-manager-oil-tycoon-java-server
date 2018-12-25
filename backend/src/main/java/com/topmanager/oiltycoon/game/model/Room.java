package com.topmanager.oiltycoon.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.topmanager.oiltycoon.game.model.GameState.PREPARE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private int maxPlayers;
    private int currentPlayers;
    private boolean isLocked;
    private boolean isTournament;
    private boolean isScenario;
    private String scenario;

    @MapKey(name="userName")
    @OneToMany(mappedBy = "room",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Map<String, Player> players;

    @OneToOne(mappedBy = "room",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Requirement requirement;

    private GameState state;
    private int maxRounds;
    private int currentRound;
    private String password;
    private int roomPeriodDelay;

    public Room(String name, int maxPlayers, boolean isLocked, boolean isTournament, boolean isScenario, String scenario,
                Requirement requirement, int maxRounds, String password, int roomPeriodDelay) {
        this(0, name, maxPlayers, 0, isLocked, isTournament, isScenario, scenario,
                new HashMap<>(), requirement, PREPARE, maxRounds, 0, password, roomPeriodDelay);
    }
}
