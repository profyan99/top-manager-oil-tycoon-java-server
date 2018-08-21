package com.topmanager.oiltycoon.dao.mapper;

import com.topmanager.oiltycoon.model.User;
import com.topmanager.oiltycoon.model.VerificationToken;

public interface UserMapper {
    User findByUserName(String userName);

    void create(User user);

    void createRoles(User user);

    User findById(int userId);

    User findByEmail(String email);

    void update(User user);

    void createVerificationToken(VerificationToken token);

    VerificationToken getVerificationToken(String uuid);

    void deleteVerificationToken(int id);
}
