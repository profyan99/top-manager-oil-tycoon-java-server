package com.topmanager.oiltycoon.game.dao.impl;

import com.topmanager.oiltycoon.game.dao.mapper.RoomMapper;
import com.topmanager.oiltycoon.social.dao.mapper.UserMapper;
import org.apache.ibatis.session.SqlSession;

public class BaseDao {
    protected RoomMapper getRoomMapper(SqlSession session) {
        return session.getMapper(RoomMapper.class);
    }

}
