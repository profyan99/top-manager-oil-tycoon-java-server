package com.topmanager.oiltycoon.game.dao;

import com.topmanager.oiltycoon.game.model.game.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyDao extends JpaRepository<Company, Integer> {
}
