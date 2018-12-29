package com.topmanager.oiltycoon.game.dao;

import com.topmanager.oiltycoon.game.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;

@Transactional
public interface RoomDao extends JpaRepository<Room, Integer> {
    boolean existsByName(String name);
}
