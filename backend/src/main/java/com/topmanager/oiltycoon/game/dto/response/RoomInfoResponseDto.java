package com.topmanager.oiltycoon.game.dto.response;

import com.topmanager.oiltycoon.game.model.GameState;
import com.topmanager.oiltycoon.game.model.Player;
import com.topmanager.oiltycoon.game.model.Requirement;
import com.topmanager.oiltycoon.game.model.Room;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@ToString
public class RoomInfoResponseDto extends BaseRoomResponseDto<RoomInfoResponseDto.RoomInfoDto> {

    public RoomInfoResponseDto(ResponseEventType eventType, RoomInfoDto body) {
        super(ResponseObjectType.ROOM_PREVIEW, eventType, body);
    }

    public static class RoomInfoDto {
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
                           boolean tournament, boolean scenario, String scenarioName, List<String> players,
                           Requirement requirement, GameState state, int maxRounds, int currentRound) {
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

        public RoomInfoDto(Room room) {
            this(
                    room.getId(),
                    room.getName(),
                    room.getMaxPlayers(),
                    room.getCurrentPlayers(),
                    room.isLocked(),
                    room.isTournament(),
                    room.isScenario(),
                    room.getScenario(),
                    room.getPlayers()
                            .values()
                            .stream()
                            .map(Player::getUserName)
                            .collect(Collectors.toList()),
                    room.getRequirement(),
                    room.getState(),
                    room.getMaxRounds(),
                    room.getCurrentRound()
            );
        }

        public RoomInfoDto() {
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
}
