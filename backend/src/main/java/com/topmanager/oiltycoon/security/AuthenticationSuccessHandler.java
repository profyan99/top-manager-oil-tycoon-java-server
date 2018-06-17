package com.topmanager.oiltycoon.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.topmanager.oiltycoon.dto.ErrorDto;
import com.topmanager.oiltycoon.dto.response.ErrorResponseDto;
import com.topmanager.oiltycoon.model.User;
import com.topmanager.oiltycoon.security.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@Component
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AuthenticationSuccessHandler() {
        super();
        setUseReferer(true);
    }

    public AuthenticationSuccessHandler(String defaultTargetUrl) {
        super(defaultTargetUrl);
        setUseReferer(true);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Object principal = authentication.getPrincipal();
        User user = null;
        if (principal instanceof SocialUserDetailsImpl) {
            user = ((SocialUserDetailsImpl) principal).getUser();
        }
        response.getOutputStream().print(objectMapper.writeValueAsString(user));
        //response.sendRedirect(request.getHeader("Referer"));
        super.onAuthenticationSuccess(request,response,authentication);

    }
}
