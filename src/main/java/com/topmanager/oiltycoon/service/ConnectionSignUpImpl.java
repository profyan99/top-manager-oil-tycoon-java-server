package com.topmanager.oiltycoon.service;

import com.topmanager.oiltycoon.dao.UserDao;
import com.topmanager.oiltycoon.model.User;
import com.topmanager.oiltycoon.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Service;

@Service
public class ConnectionSignUpImpl implements ConnectionSignUp {

    private UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public String execute(Connection<?> connection) {
        User user = new User();
        user.setEmail(connection.fetchUserProfile().getEmail());
        user.setFirstName(connection.fetchUserProfile().getFirstName());
        user.setLastName(connection.fetchUserProfile().getLastName());
        user.setNickName(connection.fetchUserProfile().getUsername());
        user.setRole(UserRole.PLAYER);
        userDao.create(user);
        return user.getId();
    }
}
