package com.topmanager.oiltycoon.game.dao;

import com.topmanager.oiltycoon.game.model.game.Factory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FactoryDao extends JpaRepository<Factory, Integer> {
}
