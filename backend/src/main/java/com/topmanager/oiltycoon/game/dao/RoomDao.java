package com.topmanager.oiltycoon.game.dao;

import com.topmanager.oiltycoon.game.model.Room;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;

@Transactional
public interface RoomDao extends CrudRepository<Room, Integer> {
    boolean existsByName(String name);
}
