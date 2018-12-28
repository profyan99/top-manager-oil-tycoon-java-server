package com.topmanager.oiltycoon.social.dao;

import com.topmanager.oiltycoon.social.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
public interface VerificationTokenDao extends JpaRepository<VerificationToken, Integer> {

    Optional<VerificationToken> findByToken(String token);
}
