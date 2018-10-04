package com.topmanager.oiltycoon.social.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.topmanager.oiltycoon.social.dto.ErrorDto;
import com.topmanager.oiltycoon.social.dto.response.ErrorResponseDto;
import com.topmanager.oiltycoon.social.security.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestAccessDeniedHandler.class);

    private ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e)
            throws IOException, ServletException {

        logger.error("Access Denied Handler");

        httpServletResponse.getOutputStream().print(objectMapper.writeValueAsString(
                new ErrorResponseDto(Collections.singletonList(new ErrorDto(ErrorCode.AUTHENTICATION_ERROR.name(),
                        ErrorCode.AUTHENTICATION_ERROR.getMessage())))
        ));

    }
}
