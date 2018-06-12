package com.topmanager.oiltycoon.security;

import com.topmanager.oiltycoon.controller.HomeController;
import com.topmanager.oiltycoon.dao.UserDao;
import com.topmanager.oiltycoon.security.SocialUserDetailsImpl;
import com.topmanager.oiltycoon.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    private UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        logger.error("LoadUserByUsername[Pure]: "+s);
        User user = userDao.findByNickName(s).orElseThrow(()->new UsernameNotFoundException("Account not found"));
        return new SocialUserDetailsImpl(user);
    }
}
