package com.topmanager.oiltycoon.social.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.topmanager.oiltycoon.social.dto.ErrorDto;
import com.topmanager.oiltycoon.social.dto.response.ErrorResponseDto;
import com.topmanager.oiltycoon.social.security.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class BaseAuthenticationEntryPoint implements AuthenticationEntryPoint {


    private static final Logger logger = LoggerFactory.getLogger(BaseAuthenticationEntryPoint.class);


    private ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException e) throws IOException {
        logger.debug("Commence entry point");
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        if(e.getCause() instanceof OAuth2Exception) {
            String errorCode = ((OAuth2Exception)e.getCause()).getOAuth2ErrorCode();
            if(errorCode.contains("invalid_token") && e.getMessage().contains("Access token expired")) {
                logger.debug("Access token expired");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().print(objectMapper.writeValueAsString(
                        new ErrorResponseDto(Collections.singletonList(new ErrorDto(ErrorCode.ACCESS_TOKEN_EXPIRED.name(),
                                ErrorCode.ACCESS_TOKEN_EXPIRED.getMessage())))
                ));
            } else {
                logger.debug("Another oauth2 exception");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().print(objectMapper.writeValueAsString(
                        new ErrorResponseDto(Collections.singletonList(new ErrorDto(ErrorCode.AUTHENTICATION_ERROR.name(),
                                ErrorCode.AUTHENTICATION_ERROR.getMessage())))
                ));
            }
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().print(objectMapper.writeValueAsString(
                    new ErrorResponseDto(Collections.singletonList(new ErrorDto(ErrorCode.AUTHENTICATION_ERROR.name(),
                            ErrorCode.AUTHENTICATION_ERROR.getMessage())))
            ));
        }
    }
}
