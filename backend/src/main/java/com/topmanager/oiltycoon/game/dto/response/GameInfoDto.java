package com.topmanager.oiltycoon.game.dto.response;

import java.util.List;

public class GameInfoDto extends BaseRoomResponseDto{
    //TODO Gamedto complete
    private List<PlayerInfoDto> players;
    private int roomId;

    public GameInfoDto(List<PlayerInfoDto> players, int roomId) {
        super(ResponseDtoType.GAME_INFO);
        this.players = players;
        this.roomId = roomId;
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
}
