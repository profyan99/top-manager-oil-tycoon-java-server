package com.topmanager.oiltycoon.game.dao;

import com.topmanager.oiltycoon.game.model.game.GasStation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GasStationDao extends JpaRepository<GasStation, Integer> {
}
