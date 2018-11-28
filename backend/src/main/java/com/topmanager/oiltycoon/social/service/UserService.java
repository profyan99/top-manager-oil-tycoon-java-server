package com.topmanager.oiltycoon.social.service;

import com.topmanager.oiltycoon.Utils;
import com.topmanager.oiltycoon.social.dao.UserDao;
import com.topmanager.oiltycoon.social.dto.UserDto;
import com.topmanager.oiltycoon.social.dto.request.ProfileEditRequestDto;
import com.topmanager.oiltycoon.social.dto.request.ResetPasswordRequestDto;
import com.topmanager.oiltycoon.social.dto.request.SignUpRequestDto;
import com.topmanager.oiltycoon.social.dto.response.GameStatsDto;
import com.topmanager.oiltycoon.social.model.*;
import com.topmanager.oiltycoon.social.security.exception.ErrorCode;
import com.topmanager.oiltycoon.social.security.exception.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private UserDao userDao;

    private PasswordEncoder passwordEncoder;

    private MailSender mailSender;

    @Autowired
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserDto getUserProfile() {
        User user = userDao.findById(getCurrentUserId()).orElseThrow(
                () -> new RestException(ErrorCode.ACCOUNT_NOT_FOUND)
        );
        logger.debug("GetUserProfile UserName: " + user.getUserName());
        return userDtoFromUserModel(user);
    }


    public void create(SignUpRequestDto dto) {
        if (userDao.findByEmail(dto.getEmail()).isPresent()) {
            throw new RestException(ErrorCode.EMAIL_NOT_UNIQUE);
        }
        if (userDao.findByUserName(dto.getUserName()).isPresent()) {
            throw new RestException(ErrorCode.USERNAME_NOT_UNIQUE);
        }
        User user = createUserFromSignUpForm(dto);
        createAndSendVerify(user);
    }

    public void createAndSendVerify(User user) {
        boolean mail = true;
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            user.getRoles().add(UserRole.WITHOUT_EMAIL);
            mail = false;
        }
        userDao.create(user);
        if (mail) {
            sendVerification(user, Utils.MailMessage.REGISTRATION_CONFIRM);
        }
    }


    public void verification(String uuid) {
        VerificationToken verificationToken = userDao.getVerificationToken(uuid).orElseThrow(
                () -> new RestException(ErrorCode.VERIFICATION_TOKEN_NOT_FOUND)
        );
        User user = verificationToken.getUser();
        if (user.getId() != getCurrentUserId()) {
            throw new RestException(ErrorCode.VERIFICATION_TOKEN_NOT_FOUND);
        }
        if (verificationToken.getConfirmDate().isBefore(LocalDateTime.now())) {
            throw new RestException(ErrorCode.CONFIRM_TIME_EXPIRED);
        }

        user.getRoles().remove(UserRole.UNVERIFIED);
        userDao.update(user);
    }

    public void updateGameStats(GameStatsDto gameStats) {
        //TODO get GameStats separate of user
    }

    public UserDto edit(ProfileEditRequestDto dto) {
        User user = userDao.findById(getCurrentUserId()).orElseThrow(
                () -> new RestException(ErrorCode.ACCOUNT_NOT_FOUND)
        );
        if (!dto.getNewPassword().isEmpty() && !dto.getOldPassword().isEmpty()) {

            if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())
                    || dto.getOldPassword().length() < 6 || dto.getOldPassword().length() > 25) {
                throw new RestException(ErrorCode.INVALID_OLD_PASSWORD);
            }
        }
        if (!user.getUserName().equals(dto.getUserName())) {
            if (userDao.findByUserName(dto.getUserName()).isPresent()) {
                throw new RestException(ErrorCode.USERNAME_NOT_UNIQUE);
            }
        }

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUserName(dto.getUserName());
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userDao.update(user);
        return userDtoFromUserModel(user);
    }

    public void forgotPassword(String email) {
        User user = userDao.findByEmail(email).orElseThrow(
                () -> new RestException(ErrorCode.ACCOUNT_NOT_FOUND)
        );
        sendVerification(user, Utils.MailMessage.RESET_PASSWORD);
    }

    public UserDto resetPassword(ResetPasswordRequestDto dto) {
        VerificationToken verificationToken = userDao.getVerificationToken(dto.getToken()).orElseThrow(
                () -> new RestException(ErrorCode.VERIFICATION_TOKEN_NOT_FOUND)
        );
        User user = verificationToken.getUser();
        if (verificationToken.getConfirmDate().isBefore(LocalDateTime.now())) {
            throw new RestException(ErrorCode.CONFIRM_TIME_EXPIRED);
        }
        if (!dto.getNewPassword().equals(dto.getNewPasswordConfirm())) {
            throw new RestException(ErrorCode.PASSWORDS_IS_NOT_EQUALS);
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userDao.update(user);
        return userDtoFromUserModel(user);
    }

    public void delete(int id) {
        User user = userDao.findById(id).orElseThrow(
                () -> new RestException(ErrorCode.ACCOUNT_NOT_FOUND)
        );
        userDao.delete(user);
    }

    public User createUserFromSignUpForm(SignUpRequestDto dto) {
        return new User(
                dto.getEmail(),
                dto.getUserName(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getPassword() == null ? null : passwordEncoder.encode(dto.getPassword()),
                new HashSet<>(Arrays.asList(UserRole.PLAYER, UserRole.UNVERIFIED)),
                new GameStats(
                        0, 0, 0, new ArrayList<>(),
                        0, 0, 0, 0, 0, new ArrayList<>()
                )
        );
    }

    private int getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.debug("getCreds: " + principal + " :: " + principal.getClass());
        if (principal instanceof UserDetails) {
            String id = ((SocialUserDetails) principal).getUserId();
            return Integer.parseInt(id);
        }
        throw new RestException(ErrorCode.ERROR_WITH_AUTHENTICATION);
    }

    private void sendVerification(User user, Utils.MailMessage mailMessage) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(
                user.getId(),
                token,
                user,
                LocalDateTime.now().plusDays(1)
        );
        userDao.createVerificationToken(verificationToken);

        mailSender.send(new MailData(
                user.getEmail(),
                mailMessage.getSubject(),
                String.format(mailMessage.getMessage(), token)
        ));
    }

    private UserDto userDtoFromUserModel(User user) {
        GameStats gameStats = user.getGameStats();
        return new UserDto(
                user.getId(),
                user.getUserName(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles(),
                user.getEmail(),
                new GameStatsDto(
                        gameStats.getId(),
                        gameStats.getGamesAmount(),
                        gameStats.getWinAmount(),
                        gameStats.getTournamentAmount(),
                        gameStats.getRewards(),
                        gameStats.getMaxRevenue(),
                        gameStats.getMaxRIF(),
                        gameStats.getHoursInGame(),
                        gameStats.getLeaveGameAmount(),
                        gameStats.getComplainAmount(),
                        gameStats.getAchievements()
                )
        );
    }
}
