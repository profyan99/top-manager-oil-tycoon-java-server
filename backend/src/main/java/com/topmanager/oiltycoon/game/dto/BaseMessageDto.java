package com.topmanager.oiltycoon.game.dto;

public class BaseMessageDto {
    private String message;

    public BaseMessageDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
