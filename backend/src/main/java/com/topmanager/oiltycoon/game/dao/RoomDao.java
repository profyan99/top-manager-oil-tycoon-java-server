package com.topmanager.oiltycoon.game.dao;

import com.topmanager.oiltycoon.game.model.Room;
import com.topmanager.oiltycoon.social.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomDao extends JpaRepository<Room, Integer> {
    boolean existsByName(String name);

    Optional<Room> findByPlayersUser(User user);
}
