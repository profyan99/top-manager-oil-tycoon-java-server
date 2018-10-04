package com.topmanager.oiltycoon.social.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.topmanager.oiltycoon.social.dto.ErrorDto;
import com.topmanager.oiltycoon.social.dto.response.ErrorResponseDto;
import com.topmanager.oiltycoon.social.security.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }



    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        logger.error("Failure Handler");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getOutputStream().print(objectMapper.writeValueAsString(
                new ErrorResponseDto(Collections.singletonList(new ErrorDto(ErrorCode.AUTHENTICATION_ERROR.name(),
                        ErrorCode.AUTHENTICATION_ERROR.getMessage())))
        ));
        super.onAuthenticationFailure(request, response, exception);
    }
}
