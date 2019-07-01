package com.topmanager.oiltycoon.game.dao;

import com.topmanager.oiltycoon.game.model.Player;
import com.topmanager.oiltycoon.social.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerDao extends JpaRepository<Player, Integer> {
    Optional<Player> findByUser(User user);
}

