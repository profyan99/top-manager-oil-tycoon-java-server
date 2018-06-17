package com.topmanager.oiltycoon.security;

import com.topmanager.oiltycoon.dao.UserDao;
import com.topmanager.oiltycoon.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class SocialUserDetailsServiceImpl implements SocialUserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(SocialUserDetailsServiceImpl.class);

    private UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public SocialUserDetails loadUserByUserId(String s) throws UsernameNotFoundException, DataAccessException {
        logger.debug("LoadUserByUserId[Social]: "+s);
        User user = userDao.findById(Integer.parseInt(s)).orElseThrow(()->new UsernameNotFoundException("Account not found"));
        return new SocialUserDetailsImpl(user);
    }
}
