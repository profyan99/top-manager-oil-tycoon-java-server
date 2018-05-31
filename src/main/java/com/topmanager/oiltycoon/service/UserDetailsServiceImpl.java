package com.topmanager.oiltycoon.service;

import com.topmanager.oiltycoon.dao.UserDao;
import com.topmanager.oiltycoon.model.SocialUserDetailsImpl;
import com.topmanager.oiltycoon.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userDao.findByNickName(s).orElseThrow(()->new UsernameNotFoundException("Account not found"));
        return new SocialUserDetailsImpl(user);
    }
}
