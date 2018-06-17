package com.topmanager.oiltycoon.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.topmanager.oiltycoon.dto.ErrorDto;
import com.topmanager.oiltycoon.dto.response.ErrorResponseDto;
import com.topmanager.oiltycoon.security.exception.ErrorCode;
import com.topmanager.oiltycoon.security.exception.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
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
                         AuthenticationException e) throws IOException, ServletException {
        logger.error("Commence entry point");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getOutputStream().print(objectMapper.writeValueAsString(
                new ErrorResponseDto(Collections.singletonList(new ErrorDto(ErrorCode.AUTHENTICATION_ERROR.name(),
                        ErrorCode.AUTHENTICATION_ERROR.getMessage())))
        ));
    }
}
