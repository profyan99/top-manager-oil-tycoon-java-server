package com.topmanager.oiltycoon.game.dao;

import com.topmanager.oiltycoon.game.model.game.scenario.Scenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScenarioDao extends JpaRepository<Scenario, Integer> {
    Optional<Scenario> findByName(String name);
}
