package com.topmanager.oiltycoon.game.dto.response;

import com.topmanager.oiltycoon.game.model.GameState;
import com.topmanager.oiltycoon.game.model.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;


public class GameInfoResponseDto extends BaseRoomResponseDto<GameInfoResponseDto.GameInfoDto> {
    public GameInfoResponseDto(ResponseEventType eventType, GameInfoDto body) {
        super(ResponseObjectType.GAME, eventType, body);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
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
        private int currentSecond;
        private int prepareSecond;
        private List<PlayerInfoResponseDto.PlayerInfoDto> players;

        public GameInfoDto(Room room) {
            this(room.getId(),
                    room.getName(),
                    room.getMaxPlayers(),
                    room.getCurrentPlayers(),
                    room.isLocked(),
                    room.isTournament(),
                    room.isScenario(),
                    room.getScenario(),
                    room.getState(),
                    room.getMaxRounds(),
                    room.getCurrentRound(),
                    room.getCurrentSecond(),
                    room.getPrepareSecond(),
                    room
                            .getPlayers()
                            .values()
                            .stream()
                            .map(PlayerInfoResponseDto.PlayerInfoDto::new)
                            .collect(Collectors.toList())

            );
        }
    }
}
