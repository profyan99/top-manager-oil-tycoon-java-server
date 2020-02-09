package com.topmanager.oiltycoon.game.dto.response;

import com.topmanager.oiltycoon.game.model.GameState;
import com.topmanager.oiltycoon.game.model.Player;
import com.topmanager.oiltycoon.game.model.Requirement;
import com.topmanager.oiltycoon.game.model.Room;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@ToString
public class RoomInfoResponseDto extends BaseRoomResponseDto<RoomInfoResponseDto.RoomInfoDto> {

    public RoomInfoResponseDto(ResponseEventType eventType, RoomInfoDto body) {
        super(ResponseObjectType.ROOM_PREVIEW, eventType, body);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class RoomInfoDto {
        private int id;
        private String name;
        private int maxPlayers;
        private int currentPlayers;
        private boolean locked;
        private boolean tournament;
        private ScenarioResponseDto scenario;
        private List<String> players;
        private Requirement requirement;
        private GameState state;
        private int maxRounds;
        private int currentRound;

        public RoomInfoDto(Room room) {
            this(
                    room.getId(),
                    room.getName(),
                    room.getMaxPlayers(),
                    room.getCurrentPlayers(),
                    room.isLocked(),
                    room.isTournament(),
                    new ScenarioResponseDto(room.getScenario()),
                    room.getPlayers()
                            .values()
                            .stream()
                            .map(Player::getUserName)
                            .collect(Collectors.toList()),
                    room.getRequirement(),
                    room.getState(),
                    room.getMaxRounds(),
                    room.getCurrentPeriod()
            );
        }

    }
}
