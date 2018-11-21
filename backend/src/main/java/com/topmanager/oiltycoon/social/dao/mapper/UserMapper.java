package com.topmanager.oiltycoon.social.dao.mapper;

import com.topmanager.oiltycoon.social.model.GameStats;
import com.topmanager.oiltycoon.social.model.User;
import com.topmanager.oiltycoon.social.model.VerificationToken;

public interface UserMapper {
    User findByUserName(String userName);

    void create(User user);

    void createRoles(User user);

    User findById(int userId);

    User findByEmail(String email);

    void update(User user);

    void createVerificationToken(VerificationToken token);

    VerificationToken getVerificationToken(String uuid);

    void deleteVerificationToken(int id);

    void delete(User user);

    void createAchievements(GameStats gameStats);

    void createRewards(GameStats gameStats);

    void createGameStats(User user);

    void updateGameStats(GameStats gameStats);
}
