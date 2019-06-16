package com.topmanager.oiltycoon.game.dto.request;

public class RoomConnectDto {
    private String password;

    public RoomConnectDto(String password) {
        this.password = password;
    }

    public RoomConnectDto() {

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
