package com.topmanager.oiltycoon.security;

import com.topmanager.oiltycoon.controller.HomeController;
import com.topmanager.oiltycoon.dao.UserDao;
import com.topmanager.oiltycoon.model.User;
import com.topmanager.oiltycoon.model.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Component
public class ConnectionSignUpImpl implements ConnectionSignUp {

    private UserDao userDao;

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public String execute(Connection<?> connection) {
        User user = new User();
        logger.error("Execute connectionSignUp "+ connection.getDisplayName());
        user.setEmail(connection.fetchUserProfile().getEmail());
        user.setFirstName(connection.fetchUserProfile().getFirstName());
        user.setLastName(connection.fetchUserProfile().getLastName());
        user.setUserName(getUniqueUserName(user.getFirstName()));
        user.setRole(UserRole.PLAYER);
        userDao.create(user);
        return user.getId();
    }

    private String getUniqueUserName(String firstName) {
        StringBuilder stringBuilder = new StringBuilder(firstName);
        int i;
        while (userDao.findByNickName(stringBuilder.toString()).isPresent()) {
            i = (int) (Math.random()*10);
            stringBuilder.append(i);
        }
        return stringBuilder.toString();
    }
}
