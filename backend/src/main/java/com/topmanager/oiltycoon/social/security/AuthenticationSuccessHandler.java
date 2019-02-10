package com.topmanager.oiltycoon.social.security;

import com.topmanager.oiltycoon.social.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;

import static com.topmanager.oiltycoon.Utils.ACCESS_TOKEN_PARAM_NAME;
import static com.topmanager.oiltycoon.Utils.REFRESH_TOKEN_PARAM_NAME;
import static com.topmanager.oiltycoon.Utils.UTF_ENCODING;

@Component
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private DefaultTokenServices defaultTokenServices;

    @Value("${security.jwt.client-id}")
    private String clientId;

    @Value("${security.jwt.resource-ids}")
    private String resourceIds;

    @Value("${frontend.url}")
    private String frontendUrl;

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setDefaultTokenServices(DefaultTokenServices defaultTokenServices) {
        this.defaultTokenServices = defaultTokenServices;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        userService.setLoggedIn();

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Object principal = authentication.getPrincipal();
        if (principal instanceof SocialUserDetailsImpl) {
            SocialUserDetailsImpl userDetails;
            userDetails = ((SocialUserDetailsImpl) principal);
            OAuth2Request oAuth2Request =
                    new OAuth2Request(
                            null, clientId, userDetails.getAuthorities(), true, null,
                            (new HashSet<String>() {{
                                add(resourceIds);
                            }}), null, null, null
                    );
            OAuth2AccessToken oauth2Token = defaultTokenServices.createAccessToken(
                    new OAuth2Authentication(
                            oAuth2Request,
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    "N/A",
                                    userDetails.getAuthorities()
                            )
                    )
            );
            logger.debug("Social auth success: " + userDetails.getUsername());
            String redirectUrl = frontendUrl+"/signin" +
                    "?" + ACCESS_TOKEN_PARAM_NAME + "=" +
                    encode(oauth2Token.getValue()) +
                    "&" + REFRESH_TOKEN_PARAM_NAME + "=" +
                    encode(oauth2Token.getRefreshToken().getValue());
            logger.debug("Sending redirection to " + redirectUrl);
            response.sendRedirect(redirectUrl);
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

    private String encode(String in) {
        return UriUtils.encode(in, UTF_ENCODING);
    }
}
