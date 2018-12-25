package com.topmanager.oiltycoon.game.dto.response;

import com.topmanager.oiltycoon.game.model.GameState;
import com.topmanager.oiltycoon.game.model.Player;
import com.topmanager.oiltycoon.game.model.Requirement;
import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class RoomInfoDto extends BaseRoomResponseDto {
    private int id;
    private String name;
    private int maxPlayers;
    private int currentPlayers;
    private boolean isLocked;
    private boolean isTournament;
    private boolean isScenario;
    private String scenario;
    private List<String> players;
    private Requirement requirement;
    private GameState state;
    private int maxRounds;
    private int currentRound;

    public RoomInfoDto(int id, String name, int maxPlayers, int currentPlayers, boolean isLocked, boolean isTournament,
                       boolean isScenario, String scenario, List<String> players, Requirement requirement, GameState state,
                       int maxRounds, int currentRound) {
        super(ResponseDtoType.ROOM_INFO);
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
    }
}
