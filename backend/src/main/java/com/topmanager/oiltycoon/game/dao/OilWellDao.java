package com.topmanager.oiltycoon.game.dao;

import com.topmanager.oiltycoon.game.model.game.OilWell;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OilWellDao extends JpaRepository<OilWell, Integer> {
}
