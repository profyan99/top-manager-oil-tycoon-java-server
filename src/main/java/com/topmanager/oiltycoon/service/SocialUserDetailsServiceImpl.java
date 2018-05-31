package com.topmanager.oiltycoon.service;

import com.topmanager.oiltycoon.dao.UserDao;
import com.topmanager.oiltycoon.model.SocialUserDetailsImpl;
import com.topmanager.oiltycoon.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class SocialUserDetailsServiceImpl implements SocialUserDetailsService {

    private UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public SocialUserDetails loadUserByUserId(String s) throws UsernameNotFoundException, DataAccessException {
        User user = userDao.findByNickName(s).orElseThrow(()->new UsernameNotFoundException("Account not found"));
        return new SocialUserDetailsImpl(user);
    }
}
