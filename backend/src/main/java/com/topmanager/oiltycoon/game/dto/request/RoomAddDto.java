package com.topmanager.oiltycoon.game.dto.request;

import com.topmanager.oiltycoon.game.model.Requirement;
import lombok.*;
import org.codehaus.jackson.annotate.JsonProperty;

public class RoomAddDto {
    private String name;
    private int maxPlayers;
    private boolean locked;
    private boolean tournament;
    private boolean scenario;
    private String scenarioName;
    private Requirement requirement;
    private int maxRounds;
    private String password;
    private int roomPeriodDelay;

    public RoomAddDto(String name, int maxPlayers, boolean locked, boolean tournament, boolean scenario, String scenarioName,
                      Requirement requirement, int maxRounds, String password, int roomPeriodDelay) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.locked = locked;
        this.tournament = tournament;
        this.scenario = scenario;
        this.scenarioName = scenarioName;
        this.requirement = requirement;
        this.maxRounds = maxRounds;
        this.password = password;
        this.roomPeriodDelay = roomPeriodDelay;
    }

    public RoomAddDto() {
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

    public Requirement getRequirement() {
        return requirement;
    }

    public void setRequirement(Requirement requirement) {
        this.requirement = requirement;
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    public void setMaxRounds(int maxRounds) {
        this.maxRounds = maxRounds;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRoomPeriodDelay() {
        return roomPeriodDelay;
    }

    public void setRoomPeriodDelay(int roomPeriodDelay) {
        this.roomPeriodDelay = roomPeriodDelay;
    }

    @Override
    public String toString() {
        return "RoomAddDto{" +
                "name='" + name + '\'' +
                ", maxPlayers=" + maxPlayers +
                ", locked=" + locked +
                ", tournament=" + tournament +
                ", scenario=" + scenario +
                ", scenarioName='" + scenarioName + '\'' +
                ", requirement=" + requirement +
                ", maxRounds=" + maxRounds +
                ", password='" + password + '\'' +
                ", roomPeriodDelay=" + roomPeriodDelay +
                '}';
    }
}
