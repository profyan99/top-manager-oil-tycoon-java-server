package com.topmanager.oiltycoon.social.security;

import com.topmanager.oiltycoon.social.dao.UserDao;
import com.topmanager.oiltycoon.social.model.User;
import com.topmanager.oiltycoon.social.security.exception.ErrorCode;
import com.topmanager.oiltycoon.social.security.exception.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private UserCache userCache;

    private UserDao userDao;

    @Autowired
    public void setUserCache(UserCache userCache) {
        this.userCache = userCache;
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String s)  {
        logger.debug("LoadUserByUsername[Pure]: "+s);
        UserDetails details = userCache.getUserFromCache(s);
        if(details == null) {
            User user = userDao.findByUserName(s).orElseThrow(()-> new RestException(ErrorCode.ACCOUNT_NOT_FOUND));
            details = new SocialUserDetailsImpl(user);
            userCache.putUserInCache(details);
            logger.debug("LoadUser from DB");
        }
        return details;
    }
}
