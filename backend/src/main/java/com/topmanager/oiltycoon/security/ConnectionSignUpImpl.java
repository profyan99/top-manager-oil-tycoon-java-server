package com.topmanager.oiltycoon.security;

import com.topmanager.oiltycoon.dao.UserDao;
import com.topmanager.oiltycoon.dto.request.SignUpRequestDto;
import com.topmanager.oiltycoon.model.User;
import com.topmanager.oiltycoon.model.UserRole;
import com.topmanager.oiltycoon.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.vkontakte.api.VKontakte;
import org.springframework.social.vkontakte.config.support.VKontakteApiHelper;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;

@Component
public class ConnectionSignUpImpl implements ConnectionSignUp {

    private UserDao userDao;

    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(ConnectionSignUpImpl.class);

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String execute(Connection<?> connection) {
        logger.info("Execute connectionSignUp " + connection.getDisplayName());
        logger.debug("SignUp email: " + connection.fetchUserProfile().getEmail());
        String email;

        //Some bugs with fetchUserProfile.getEmail in Vk library
        if (connection.getApi() instanceof VKontakte) {
            email = ((VKontakte) connection.getApi()).getEmail();
        } else {
            email = connection.fetchUserProfile().getEmail();
        }
        User user = userService.createUserFromSignUpForm(new SignUpRequestDto(
                        getUniqueUserName(connection.fetchUserProfile().getFirstName()),
                        null,
                        connection.fetchUserProfile().getFirstName(),
                        connection.fetchUserProfile().getLastName(),
                        email
                )
        );
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            user.getRoles().add(UserRole.WITHOUT_EMAIL);
        }
        userDao.create(user);
        logger.debug(user.toString());
        return String.valueOf(user.getId());
    }

    private String getUniqueUserName(String firstName) {
        StringBuilder stringBuilder = new StringBuilder(firstName);
        int i;
        while (userDao.findByUserName(stringBuilder.toString()).isPresent()) {
            i = (int) (Math.random() * 10);
            stringBuilder.append(i);
        }
        return stringBuilder.toString();
    }
}
