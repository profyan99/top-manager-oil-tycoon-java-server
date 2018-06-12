package com.topmanager.oiltycoon.dao;

import com.topmanager.oiltycoon.model.User;

import java.util.Optional;

public interface UserDao {
    Optional<User> findByNickName(String nickName);

    void create(User user);

    Optional<User> findById(int userId);
}
