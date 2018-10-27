package com.topmanager.oiltycoon.social.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.topmanager.oiltycoon.social.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;

@Component
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private ObjectMapper objectMapper;

    private DefaultTokenServices defaultTokenServices;

    @Value("${security.jwt.client-id}")
    private String clientId;

    @Value("${security.jwt.resource-ids}")
    private String resourceIds;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Autowired
    public void setDefaultTokenServices(DefaultTokenServices defaultTokenServices) {
        this.defaultTokenServices = defaultTokenServices;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AuthenticationSuccessHandler() {
        super();
        logger.error(":: "+(frontendUrl+"/signin"));
        setDefaultTargetUrl("http://localhost:8080/signin");
        setAlwaysUseDefaultTargetUrl(true);
    }

    public AuthenticationSuccessHandler(String defaultTargetUrl) {
        super(defaultTargetUrl);
        setDefaultTargetUrl("http://localhost:8080/signin");
        setAlwaysUseDefaultTargetUrl(true);
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Object principal = authentication.getPrincipal();
        if (principal instanceof SocialUserDetailsImpl) {
            SocialUserDetailsImpl userDetails;
            userDetails = ((SocialUserDetailsImpl) principal);
            OAuth2Request oAuth2Request =
                    new OAuth2Request(
                            null, clientId, userDetails.getAuthorities(), true, null,
                            (new HashSet<String>() {{ add(resourceIds); }}), null, null, null
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
            String body = objectMapper.writeValueAsString(oauth2Token);
            logger.debug("Social auth success: "+userDetails.getUsername());
            response.getOutputStream().print(body);
        }
        super.onAuthenticationSuccess(request,response,authentication);
    }
}
