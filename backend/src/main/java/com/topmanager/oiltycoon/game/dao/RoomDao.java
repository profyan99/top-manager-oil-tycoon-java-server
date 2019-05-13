package com.topmanager.oiltycoon.game.dao;

import com.topmanager.oiltycoon.game.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;

@Transactional
@Repository
public interface RoomDao extends CrudRepository<Room, Integer> {
    boolean existsByName(String name);
}
