package com.topmanager.oiltycoon.game.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.topmanager.oiltycoon.game.model.GameState.PREPARE;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @JsonManagedReference
    private Requirement requirement;

    private GameState state;
    private int maxRounds;
    private int currentRound;
    private String password;
    private int roomPeriodDelay;

    public Room(int id, String name, int maxPlayers, int currentPlayers, boolean isLocked, boolean isTournament, boolean isScenario,
                String scenario, Map<String, Player> players, Requirement requirement, GameState state, int maxRounds,
                int currentRound, String password, int roomPeriodDelay) {
        this.id = id;
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.currentPlayers = currentPlayers;
        this.isLocked = isLocked;
        this.isTournament = isTournament;
        this.isScenario = isScenario;
        this.scenario = scenario;
        this.players = players;
        this.requirement = requirement;
        this.state = state;
        this.maxRounds = maxRounds;
        this.currentRound = currentRound;
        this.password = password;
        this.roomPeriodDelay = roomPeriodDelay;
        requirement.setRoom(this);
    }


    public Room(String name, int maxPlayers, boolean isLocked, boolean isTournament, boolean isScenario, String scenario,
                Requirement requirement, int maxRounds, String password, int roomPeriodDelay) {
        this(0, name, maxPlayers, 0, isLocked, isTournament, isScenario, scenario,
                new HashMap<>(), requirement, PREPARE, maxRounds, 0, password, roomPeriodDelay);
    }
}
