package com.topmanager.oiltycoon.dao.mapper;

import com.topmanager.oiltycoon.model.User;

public interface UserMapper {
    User findByNickName(String userName);

    void create(User user);

    User findById(int userId);
}
