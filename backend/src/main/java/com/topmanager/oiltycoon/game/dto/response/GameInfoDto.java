package com.topmanager.oiltycoon.game.dto.response;

import java.util.List;

public class GameInfoDto extends BaseRoomResponseDto{
    //TODO Gamedto complete
    private List<PlayerInfoDto> players;

    public GameInfoDto(List<PlayerInfoDto> players) {
        super(ResponseDtoType.GAME_INFO);
        this.players = players;
    }

    public List<PlayerInfoDto> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerInfoDto> players) {
        this.players = players;
    }
}
