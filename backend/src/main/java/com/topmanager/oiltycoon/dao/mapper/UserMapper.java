package com.topmanager.oiltycoon.dao.mapper;

import com.topmanager.oiltycoon.model.User;

public interface UserMapper {
    User findByUserName(String userName);

    void create(User user);

    void createRoles(User user);

    User findById(int userId);

    User findByEmail(String email);
}
