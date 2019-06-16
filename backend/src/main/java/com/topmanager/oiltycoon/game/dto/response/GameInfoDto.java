package com.topmanager.oiltycoon.game.dto.response;

import com.topmanager.oiltycoon.game.model.GameState;

import java.util.List;


public class GameInfoDto extends BaseRoomResponseDto{
    //TODO Gamedto complete
    private int roomId;
    private String name;
    private int maxPlayers;
    private int currentPlayers;
    private boolean locked;
    private boolean tournament;
    private boolean scenario;
    private String scenarioName;
    private GameState state;
    private int maxRounds;
    private int currentRound;
    private List<PlayerInfoDto> players;

    public GameInfoDto(int roomId, String name, int maxPlayers, int currentPlayers, boolean locked,
                       boolean tournament, boolean scenario, String scenarioName, GameState state, int maxRounds,
                       int currentRound, List<PlayerInfoDto> players) {
        super(ResponseDtoType.GAME_INFO);
        this.roomId = roomId;
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.currentPlayers = currentPlayers;
        this.locked = locked;
        this.tournament = tournament;
        this.scenario = scenario;
        this.scenarioName = scenarioName;
        this.state = state;
        this.maxRounds = maxRounds;
        this.currentRound = currentRound;
        this.players = players;
    }

    public List<PlayerInfoDto> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerInfoDto> players) {
        this.players = players;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getCurrentPlayers() {
        return currentPlayers;
    }

    public void setCurrentPlayers(int currentPlayers) {
        this.currentPlayers = currentPlayers;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isTournament() {
        return tournament;
    }

    public void setTournament(boolean tournament) {
        this.tournament = tournament;
    }

    public boolean isScenario() {
        return scenario;
    }

    public void setScenario(boolean scenario) {
        this.scenario = scenario;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    public void setMaxRounds(int maxRounds) {
        this.maxRounds = maxRounds;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }
}
