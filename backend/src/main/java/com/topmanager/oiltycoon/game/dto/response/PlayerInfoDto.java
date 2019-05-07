package com.topmanager.oiltycoon.game.dto.response;


import com.topmanager.oiltycoon.game.model.Player;

public class PlayerInfoDto extends BaseRoomResponseDto {
    private String userName;
    private String avatar;
    private int id;

    public PlayerInfoDto(Player player, ResponseDtoType type) {
        super(type);
        this.userName = player.getUserName();
        this.avatar = player.getUser().getAvatar();
        this.id = player.getUser().getId();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
