package com.topmanager.oiltycoon.service;

import com.topmanager.oiltycoon.dao.UserDao;
import com.topmanager.oiltycoon.dto.UserDto;
import com.topmanager.oiltycoon.model.User;
import com.topmanager.oiltycoon.security.exception.ErrorCode;
import com.topmanager.oiltycoon.security.exception.RestException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserDto getUserProfile() {
        User user = userDao.findById(getCurrentUserId()).orElseThrow(
                () -> new RestException(ErrorCode.ACCOUNT_NOT_FOUND)
        );
        return new UserDto(
                user.getId(),
                user.getUserName(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        );
    }

    private int getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String id = ((SocialUserDetails) principal).getUserId();
            return Integer.parseInt(id);
        }
        throw new RestException(ErrorCode.ERROR_WITH_AUTHENTICATION);
    }
}
