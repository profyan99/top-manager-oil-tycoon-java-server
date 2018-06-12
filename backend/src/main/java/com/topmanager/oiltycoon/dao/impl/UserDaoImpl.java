package com.topmanager.oiltycoon.dao.impl;

import com.topmanager.oiltycoon.dao.UserDao;
import com.topmanager.oiltycoon.model.User;
import com.topmanager.oiltycoon.security.exception.ErrorCode;
import com.topmanager.oiltycoon.security.exception.RestException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
public class UserDaoImpl extends BaseDao implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    private SqlSessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<User> findByNickName(String nickName) {
        try (SqlSession session = sessionFactory.openSession()) {
            return Optional.ofNullable(getUserMapper(session).findByNickName(nickName));
        } catch (RuntimeException e) {
            logger.error("Couldn't find by nickname: " + e.toString());
            throw new RestException(ErrorCode.ERROR_WITH_DAtABASE);
        }
    }

    @Override
    public void create(User user) {
        try (SqlSession session = sessionFactory.openSession()) {
            getUserMapper(session).create(user);
            session.commit();
        } catch (RuntimeException e) {
            logger.error("Couldn't create user: " + e.toString());
            throw new RestException(ErrorCode.ERROR_WITH_DAtABASE);
        }
    }

    @Override
    public Optional<User> findById(int userId) {
        try (SqlSession session = sessionFactory.openSession()) {
            return Optional.ofNullable(getUserMapper(session).findById(userId));
        } catch (RuntimeException e) {
            logger.error("Couldn't find by id: " + e.toString());
            throw new RestException(ErrorCode.ERROR_WITH_DAtABASE);
        }
    }
}
