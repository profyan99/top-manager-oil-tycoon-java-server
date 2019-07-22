package com.topmanager.oiltycoon.game.dao;

import com.topmanager.oiltycoon.game.model.game.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreDao extends JpaRepository<Store, Integer> {
}
