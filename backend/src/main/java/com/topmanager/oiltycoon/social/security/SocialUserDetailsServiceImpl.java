package com.topmanager.oiltycoon.social.security;

import com.topmanager.oiltycoon.social.dao.UserDao;
import com.topmanager.oiltycoon.social.model.User;
import com.topmanager.oiltycoon.social.security.exception.ErrorCode;
import com.topmanager.oiltycoon.social.security.exception.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class SocialUserDetailsServiceImpl implements SocialUserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(SocialUserDetailsServiceImpl.class);

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
    public SocialUserDetails loadUserByUserId(String s) throws RestException {
        logger.debug("LoadUserByUserId[Social]: "+s);
        SocialUserDetails details = (SocialUserDetails) userCache.getUserFromCache(s);
        if(details == null) {
            User user = userDao.findById(Integer.parseInt(s)).orElseThrow(()->new RestException(ErrorCode.ACCOUNT_NOT_FOUND));
            details = new SocialUserDetailsImpl(user);
            userCache.putUserInCache(details);
            logger.debug("LoadUser from DB");
        }
        return details;
    }
}
