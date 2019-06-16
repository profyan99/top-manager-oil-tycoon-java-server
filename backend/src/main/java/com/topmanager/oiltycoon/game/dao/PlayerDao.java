package com.topmanager.oiltycoon.game.dao;

import com.topmanager.oiltycoon.game.model.Player;
import com.topmanager.oiltycoon.social.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PlayerDao extends CrudRepository<Player, Integer> {
    Optional<Player> findByUser(User user);
}

