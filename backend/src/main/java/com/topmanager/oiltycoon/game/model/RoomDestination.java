package com.topmanager.oiltycoon.game.model;

public enum RoomDestination {
    ADD_ROOM("/topic/addRoom")
    ;
    private String destination;

    public String getDestination() {
        return destination;
    }

    RoomDestination(String destination) {
        this.destination = destination;
    }
}
