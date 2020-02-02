package com.topmanager.oiltycoon.game.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.topmanager.oiltycoon.game.model.game.GamePeriodData;
import com.topmanager.oiltycoon.game.model.game.scenario.Scenario;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

import static com.topmanager.oiltycoon.game.model.GameState.PREPARE;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private int maxPlayers;
    private int currentPlayers;
    private boolean isLocked;
    private boolean isTournament;
    private boolean isScenario;

    private int currentSecond;
    private int prepareSecond;
    private int timePlayerReload;

    @MapKey(name = "userName")
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "room",
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.PERSIST,
                    CascadeType.DETACH,
                    CascadeType.REFRESH
            },
            orphanRemoval = true
    )
    private Map<String, Player> players;

    @Embedded
    @JsonManagedReference
    private Requirement requirement;

    private GameState state;
    private int maxRounds;
    private int currentRound;
    private String password;
    private int roomPeriodDelay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;

    @ElementCollection
    @CollectionTable(
            name = "game_data",
            joinColumns = @JoinColumn(name = "room_id"),
            foreignKey = @ForeignKey(name = "game_data_game_fk")
    )
    private Map<Integer, GamePeriodData> periodData;
    private boolean isSendSolutionAllowed;
    private int playersSolutionSentAmount;

    public Room(Integer id, String name, int maxPlayers, int currentPlayers, boolean isLocked, boolean isTournament, boolean isScenario,
                Scenario scenario, Map<String, Player> players, GameState state, int maxRounds,
                int currentRound, String password, int roomPeriodDelay, Requirement requirement) {
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

        this.currentSecond = 0;
        this.timePlayerReload = roomPeriodDelay * 2;
        this.prepareSecond = this.timePlayerReload;
        this.periodData = new HashMap<>();
    }


    public Room(String name, int maxPlayers, boolean isLocked, boolean isTournament, boolean isScenario, Scenario scenario,
                Requirement requirement, int maxRounds, String password, int roomPeriodDelay) {
        this(null, name, maxPlayers, 0, isLocked, isTournament, isScenario, scenario,
                new HashMap<>(), PREPARE, maxRounds, 0, password, roomPeriodDelay, requirement);
    }

    public void addPlayer(Player player) {
        players.put(player.getUserName(), player);
        player.setRoom(this);
        currentPlayers++;
    }

    public void removePlayer(Player player) {
        players.remove(player.getUserName(), player);
        currentPlayers--;
    }

    public int incCurrentSecond() {
        return ++currentSecond;
    }

    public int decPrepareSecond() {
        return --prepareSecond;
    }

    public int incPlayersSolutionSentAmount() {
        return ++playersSolutionSentAmount;
    }

    public Requirement getRequirement() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Room))
            return false;

        return id != null && id.equals(((Room) o).getId());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public GamePeriodData getPeriodDataByPeriod(int period) {
        return periodData.get(period);
    }
}
