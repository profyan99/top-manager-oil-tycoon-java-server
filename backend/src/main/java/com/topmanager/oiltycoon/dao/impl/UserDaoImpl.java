package com.topmanager.oiltycoon.dao.impl;

import com.topmanager.oiltycoon.dao.UserDao;
import com.topmanager.oiltycoon.model.User;
import com.topmanager.oiltycoon.model.VerificationToken;
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
    public Optional<User> findByUserName(String userName) {
        try (SqlSession session = sessionFactory.openSession()) {
            return Optional.ofNullable(getUserMapper(session).findByUserName(userName));
        } catch (RuntimeException e) {
            logger.error("Couldn't find by userName: " + e.toString());
            throw new RestException(ErrorCode.ERROR_WITH_DAtABASE);
        }
    }

    @Override
    public void create(User user) {
        try (SqlSession session = sessionFactory.openSession()) {
            getUserMapper(session).create(user);
            getUserMapper(session).createRoles(user);
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

    @Override
    public Optional<User> findByEmail(String email) {
        try (SqlSession session = sessionFactory.openSession()) {
            return Optional.ofNullable(getUserMapper(session).findByEmail(email));
        } catch (RuntimeException e) {
            logger.error("Couldn't find by email: " + e.toString());
            throw new RestException(ErrorCode.ERROR_WITH_DAtABASE);
        }
    }

    @Override
    public void update(User user) {
        try (SqlSession session = sessionFactory.openSession()) {
            getUserMapper(session).update(user);
            session.commit();
        } catch (RuntimeException e) {
            logger.error("Couldn't update user: " + e.toString());
            throw new RestException(ErrorCode.ERROR_WITH_DAtABASE);
        }
    }

    @Override
    public void createVerificationToken(VerificationToken token) {
        try (SqlSession session = sessionFactory.openSession()) {
            getUserMapper(session).createVerificationToken(token);
            session.commit();
        } catch (RuntimeException e) {
            logger.error("Couldn't create verification token: " + e.toString());
            throw new RestException(ErrorCode.ERROR_WITH_DAtABASE);
        }
    }

    @Override
    public Optional<VerificationToken> getVerificationToken(String uuid) {
        try (SqlSession session = sessionFactory.openSession()) {
            return Optional.ofNullable(getUserMapper(session).getVerificationToken(uuid));
        } catch (RuntimeException e) {
            logger.error("Couldn't find verification token by uuid: " + e.toString());
            throw new RestException(ErrorCode.ERROR_WITH_DAtABASE);
        }
    }
}
