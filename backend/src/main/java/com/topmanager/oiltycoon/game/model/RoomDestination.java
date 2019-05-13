package com.topmanager.oiltycoon.game.model;

public class RoomDestination {
    public static final String BROKER_DESTINATION_PREFIX = "/topic";
    public static final String USER_DESTINATION_PREFIX = "/user";
    public static final String ROOM_BASE_ENDPOINT = "/room";

    //websocket topics
    public static final String ROOM_EVENT = ROOM_BASE_ENDPOINT +"/event";
    public static final String ROOM_LIST = ROOM_BASE_ENDPOINT +"/list";
}
