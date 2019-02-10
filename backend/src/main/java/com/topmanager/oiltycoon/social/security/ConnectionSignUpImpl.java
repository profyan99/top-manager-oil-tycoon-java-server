package com.topmanager.oiltycoon.social.security;

import com.topmanager.oiltycoon.social.dao.UserDao;
import com.topmanager.oiltycoon.social.dto.request.SignUpRequestDto;
import com.topmanager.oiltycoon.social.model.User;
import com.topmanager.oiltycoon.social.security.exception.ErrorCode;
import com.topmanager.oiltycoon.social.security.exception.RestException;
import com.topmanager.oiltycoon.social.service.UserService;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.account.UserXtrContact;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.queries.account.AccountGetInfoField;
import com.vk.api.sdk.queries.users.UserField;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.plus.Person;
import org.springframework.social.vkontakte.api.VKontakte;
import org.springframework.social.vkontakte.config.support.VKontakteApiHelper;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ConnectionSignUpImpl implements ConnectionSignUp {

    private UserDao userDao;

    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(ConnectionSignUpImpl.class);

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String execute(Connection<?> connection) {
        logger.info("Execute connectionSignUp " + connection.getDisplayName());
        logger.debug("SignUp email: " + connection.fetchUserProfile().getEmail());
        String email;
        String country = null;

        if (connection.getApi() instanceof VKontakte) {
            VKontakte vKontakteApi = (VKontakte) connection.getApi();
            email = vKontakteApi.getEmail();
            VkApiClient vk = new VkApiClient(HttpTransportClient.getInstance());
            try {
                UserXtrCounters counters = vk.users().get(vKontakteApi.getUserActor())
                        .fields(UserField.COUNTRY).execute().get(0);
                country = counters.getCountry().getTitle();
            } catch (ApiException | ClientException e) {
                throw new RestException(ErrorCode.AUTHORIZATION_ERROR);
            }
        } else {
            Google googleApi = (Google) connection.getApi();
            email = connection.fetchUserProfile().getEmail();
            Person googleApiProfile =  googleApi
                    .plusOperations()
                    .getGoogleProfile();
            Map<String, Boolean> places = googleApiProfile.getPlacesLived();

            if (places != null) {
                country = places.entrySet()
                        .stream()
                        .filter(Map.Entry::getValue)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.joining(" "));
            }
        }
        User user = userService.createUserFromSignUpForm(new SignUpRequestDto(
                        getUniqueUserName(connection.fetchUserProfile().getFirstName()),
                        null,
                        connection.fetchUserProfile().getFirstName(),
                        connection.fetchUserProfile().getLastName(),
                        email,
                        "",
                        connection.getImageUrl(),
                        country
                )
        );
        userService.createAndSendVerify(user);
        logger.debug(user.toString());
        return String.valueOf(user.getId());
    }

    private String getUniqueUserName(String firstName) {
        StringBuilder stringBuilder = new StringBuilder(firstName);
        int i;
        while (userDao.findByUserName(stringBuilder.toString()).isPresent()) {
            i = (int) (Math.random() * 10);
            stringBuilder.append(i);
        }
        return stringBuilder.toString();
    }
}
