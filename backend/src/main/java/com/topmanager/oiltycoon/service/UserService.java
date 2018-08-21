package com.topmanager.oiltycoon.service;

import com.topmanager.oiltycoon.Utils;
import com.topmanager.oiltycoon.dao.UserDao;
import com.topmanager.oiltycoon.dto.UserDto;
import com.topmanager.oiltycoon.dto.request.ProfileEditRequestDto;
import com.topmanager.oiltycoon.dto.request.SignUpRequestDto;
import com.topmanager.oiltycoon.model.MailData;
import com.topmanager.oiltycoon.model.User;
import com.topmanager.oiltycoon.model.UserRole;
import com.topmanager.oiltycoon.model.VerificationToken;
import com.topmanager.oiltycoon.security.exception.ErrorCode;
import com.topmanager.oiltycoon.security.exception.RestException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
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

        return new UserDto(
                user.getId(),
                user.getUserName(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles()
        );
    }



    public UserDto create(SignUpRequestDto dto) {
        if(userDao.findByEmail(dto.getEmail()).isPresent()) {
            throw new RestException(ErrorCode.EMAIL_NOT_UNIQUE);
        }
        if(userDao.findByUserName(dto.getUserName()).isPresent()) {
            throw new RestException(ErrorCode.USERNAME_NOT_UNIQUE);
        }
        User user = createUserFromSignUpForm(dto);
        createAndSendVerify(user);
        authenticateUser(user);
        return new UserDto(
                user.getId(),
                user.getUserName(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles()
        );
    }

    public void createAndSendVerify(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            user.getRoles().add(UserRole.WITHOUT_EMAIL);
            userDao.create(user);
        }
        else {
            userDao.create(user);
            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken(
                    user.getId(),
                    token,
                    user,
                    LocalDateTime.now().plusDays(1)
            );
            userDao.createVerificationToken(verificationToken);
            //TODO delete magic strings
            String recipientAddress = user.getEmail();
            String subject = "Registration Confirmation";
            String confirmationUrl
                    = Utils.BASE_URL + "/verification?token=" + token;
            String message = "Verify your account: ";
            mailSender.send(new MailData(
                    recipientAddress,
                    subject,
                    message + " link: "+confirmationUrl
            ));
        }
    }

    public void verification(String uuid) {
        VerificationToken verificationToken = userDao.getVerificationToken(uuid).orElseThrow(
                () -> new RestException(ErrorCode.VERIFICATION_TOKEN_NOT_FOUND)
        );
        User user = verificationToken.getUser();
        if(verificationToken.getConfirmDate().isBefore(LocalDateTime.now())) {
            throw new RestException(ErrorCode.CONFIRM_TIME_EXPIRED);
        }
        user.getRoles().remove(UserRole.UNVERIFIED);
        userDao.update(user);
    }

    public UserDto edit(ProfileEditRequestDto dto) {
        User user = userDao.findById(getCurrentUserId()).orElseThrow(
                () -> new RestException(ErrorCode.ACCOUNT_NOT_FOUND)
        );
        if(!user.getPassword().equals(passwordEncoder.encode(dto.getOldPassword()))) {
            throw new RestException(ErrorCode.INVALID_OLD_PASSWORD);
        }
        if(userDao.findByUserName(dto.getUserName()).isPresent()) {
            throw new RestException(ErrorCode.USERNAME_NOT_UNIQUE);
        }
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUserName(dto.getUserName());
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userDao.update(user);
        authenticateUser(user);
        return new UserDto(
                user.getId(),
                user.getUserName(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles()
        );
    }


    private void authenticateUser(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(r -> authorities.add(new SimpleGrantedAuthority(r.name())));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword(), authorities));

    }

    public User createUserFromSignUpForm(SignUpRequestDto dto) {
        return new User(
                dto.getEmail(),
                dto.getUserName(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getPassword() == null ? null : passwordEncoder.encode(dto.getPassword()),
                new HashSet<>(Arrays.asList(UserRole.PLAYER, UserRole.UNVERIFIED))
        );
    }

    private int getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String id = ((SocialUserDetails) principal).getUserId();
            return Integer.parseInt(id);
        }
        throw new RestException(ErrorCode.ERROR_WITH_AUTHENTICATION);
    }
}
