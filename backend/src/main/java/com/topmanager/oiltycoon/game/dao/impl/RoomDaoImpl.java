package com.topmanager.oiltycoon.game.dao.impl;

import com.topmanager.oiltycoon.game.dao.RoomDao;
import com.topmanager.oiltycoon.game.model.Room;
import com.topmanager.oiltycoon.social.dao.impl.UserDaoImpl;
import com.topmanager.oiltycoon.social.security.exception.ErrorCode;
import com.topmanager.oiltycoon.social.security.exception.RestException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public class RoomDaoImpl extends BaseDao implements RoomDao {

    private static final Logger logger = LoggerFactory.getLogger(RoomDaoImpl.class);

    private SqlSessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Set<Room> findAllRooms() {
        try (SqlSession session = sessionFactory.openSession()) {
            return getRoomMapper(session).findAllRooms();
        } catch (RuntimeException e) {
            logger.error("Couldn't find all rooms: " + e.toString());
            throw new RestException(ErrorCode.ERROR_WITH_DATABASE);
        }
    }

    @Override
    public void addRoom(Room room) {
        try (SqlSession session = sessionFactory.openSession()) {
            getRoomMapper(session).addRoom(room);
            session.commit();
        } catch (RuntimeException e) {
            logger.error("Couldn't add room: " + e.toString());
            throw new RestException(ErrorCode.ERROR_WITH_DATABASE);
        }
    }

    @Override
    public void deleteRoomById(Room room) {
        try (SqlSession session = sessionFactory.openSession()) {
            getRoomMapper(session).deleteRoomById(room);
            session.commit();
        } catch (RuntimeException e) {
            logger.error("Couldn't delete room: " + e.toString());
            throw new RestException(ErrorCode.ERROR_WITH_DATABASE);
        }
    }

    @Override
    public void updateRoom(Room room) {
        try (SqlSession session = sessionFactory.openSession()) {
            getRoomMapper(session).updateRoom(room);
            session.commit();
        } catch (RuntimeException e) {
            logger.error("Couldn't update room: " + e.toString());
            throw new RestException(ErrorCode.ERROR_WITH_DATABASE);
        }
    }

    @Override
    public boolean existsByName(String name) {
        try (SqlSession session = sessionFactory.openSession()) {
            return getRoomMapper(session).existsByName(name);
        } catch (RuntimeException e) {
            logger.error("Couldn't check existing room: " + e.toString());
            throw new RestException(ErrorCode.ERROR_WITH_DATABASE);
        }
    }
}
