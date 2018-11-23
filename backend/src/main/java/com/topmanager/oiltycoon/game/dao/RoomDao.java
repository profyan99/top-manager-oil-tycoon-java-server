package com.topmanager.oiltycoon.game.dao;

import com.topmanager.oiltycoon.game.model.Room;

import java.util.Set;

public interface RoomDao {
    Set<Room> findAllRooms();

    void addRoom(Room room);

    void deleteRoomById(Room room);

    void updateRoom(Room room);

    boolean existsByName(String name);
}
