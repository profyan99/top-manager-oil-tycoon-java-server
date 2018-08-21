package com.topmanager.oiltycoon.dao;

import com.topmanager.oiltycoon.model.User;
import com.topmanager.oiltycoon.model.VerificationToken;

import java.util.Optional;

public interface UserDao {
    Optional<User> findByUserName(String userName);

    void create(User user);

    Optional<User> findById(int userId);

    Optional<User> findByEmail(String email);

    void update(User user);

    void createVerificationToken(VerificationToken token);

    Optional<VerificationToken> getVerificationToken(String uuid);
}
