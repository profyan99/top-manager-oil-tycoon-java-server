package com.topmanager.oiltycoon.game.dto.response;

import com.topmanager.oiltycoon.game.model.GameState;

import java.util.List;


public class GameInfoResponseDto extends BaseRoomResponseDto<GameInfoResponseDto.GameInfoDto> {
    public GameInfoResponseDto(ResponseEventType eventType, GameInfoDto body) {
        super(ResponseObjectType.GAME, eventType, body);
    }

    //TODO Gamedto complete
    public static class GameInfoDto {
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
        private List<PlayerInfoResponseDto.PlayerInfoDto> players;

        public GameInfoDto(int roomId, String name, int maxPlayers, int currentPlayers, boolean locked,
                           boolean tournament, boolean scenario, String scenarioName, GameState state, int maxRounds,
                           int currentRound, List<PlayerInfoResponseDto.PlayerInfoDto> players) {
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

        public GameInfoDto() {
        }

        public List<PlayerInfoResponseDto.PlayerInfoDto> getPlayers() {
            return players;
        }

        public int getRoomId() {
            return roomId;
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
}
