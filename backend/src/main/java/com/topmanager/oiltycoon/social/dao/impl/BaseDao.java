package com.topmanager.oiltycoon.social.dao.impl;

import com.topmanager.oiltycoon.social.dao.mapper.UserMapper;
import org.apache.ibatis.session.SqlSession;

public class BaseDao {
    protected UserMapper getUserMapper(SqlSession session) {
        return session.getMapper(UserMapper.class);
    }
}
