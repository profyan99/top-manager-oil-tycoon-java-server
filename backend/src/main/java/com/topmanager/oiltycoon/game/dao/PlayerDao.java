package com.topmanager.oiltycoon.game.dao;

import com.topmanager.oiltycoon.game.model.Player;
import org.springframework.data.repository.CrudRepository;

public interface PlayerDao extends CrudRepository<Player, Integer> {
}
