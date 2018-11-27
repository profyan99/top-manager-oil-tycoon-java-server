package com.topmanager.oiltycoon.game.dao.mapper;

import com.topmanager.oiltycoon.game.model.Room;

import java.util.Set;

public interface RoomMapper {
    Set<Room> findAllRooms();

    Room findRoomById(int id);

    void addRoom(Room room);

    void deleteRoomById(Room room);

    void updateRoom(Room room);

    boolean existsByName(String name);
}
