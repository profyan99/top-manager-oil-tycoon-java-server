package com.topmanager.oiltycoon.game.dto.response;

import com.topmanager.oiltycoon.game.model.GameState;
import com.topmanager.oiltycoon.game.model.Player;
import com.topmanager.oiltycoon.game.model.Requirement;
import lombok.*;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString
public class RoomInfoDto extends BaseRoomResponseDto {
    private int id;
    private String name;
    private int maxPlayers;
    private int currentPlayers;
    private boolean locked;
    private boolean tournament;
    private boolean scenario;
    private String scenarioName;
    private List<String> players;
    private Requirement requirement;
    private GameState state;
    private int maxRounds;
    private int currentRound;

    public RoomInfoDto(int id, String name, int maxPlayers, int currentPlayers, boolean locked,
                       boolean tournament, boolean scenario, String scenarioName, List<String> players, Requirement requirement, GameState state, int maxRounds, int currentRound) {
        super(ResponseDtoType.ROOM_INFO);
        this.id = id;
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.currentPlayers = currentPlayers;
        this.locked = locked;
        this.tournament = tournament;
        this.scenario = scenario;
        this.scenarioName = scenarioName;
        this.players = players;
        this.requirement = requirement;
        this.state = state;
        this.maxRounds = maxRounds;
        this.currentRound = currentRound;
    }

    public RoomInfoDto() {
        super(ResponseDtoType.ROOM_INFO);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getCurrentPlayers() {
        return currentPlayers;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isTournament() {
        return tournament;
    }

    public boolean isScenario() {
        return scenario;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public List<String> getPlayers() {
        return players;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public GameState getState() {
        return state;
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    public int getCurrentRound() {
        return currentRound;
    }
}
