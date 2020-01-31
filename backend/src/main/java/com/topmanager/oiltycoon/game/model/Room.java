package com.topmanager.oiltycoon.game.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
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
    private String scenario;

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

    @ElementCollection
    @CollectionTable(
            name = "game_data",
            joinColumns = @JoinColumn(name = "room_id"),
            foreignKey = @ForeignKey(name = "game_data_game_fk")
    )
    private Map<Integer, GameData> periodData;
    private boolean isSendSolutionAllowed;

    public Room(Integer id, String name, int maxPlayers, int currentPlayers, boolean isLocked, boolean isTournament, boolean isScenario,
                String scenario, Map<String, Player> players, GameState state, int maxRounds,
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


    public Room(String name, int maxPlayers, boolean isLocked, boolean isTournament, boolean isScenario, String scenario,
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

    public void incCurrentSecond() {
        currentSecond++;
    }

    public void decPrepareSecond() {
        prepareSecond--;
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

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Embeddable
    public static class GameData {
        private int summaryMarketing;
        private int summaryNir;
        private int summaryProduction;
        private double totalMarketing;
        private double totalPrice;
        private int totalBuyers;
        private int totalSales;
    }
}
