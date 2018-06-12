package com.topmanager.oiltycoon.dao.impl;

import com.topmanager.oiltycoon.dao.mapper.UserMapper;
import org.apache.ibatis.session.SqlSession;

public class BaseDao {
    protected UserMapper getUserMapper(SqlSession session) {
        return session.getMapper(UserMapper.class);
    }
}
