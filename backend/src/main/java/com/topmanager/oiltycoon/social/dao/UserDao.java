package com.topmanager.oiltycoon.social.dao;

import com.topmanager.oiltycoon.social.model.User;
import com.topmanager.oiltycoon.social.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
public interface UserDao extends JpaRepository<User, Integer> {

    Optional<User> findByUserName(String userName);

    Optional<User> findByEmail(String email);

}
