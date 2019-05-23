package com.topmanager.oiltycoon;


import com.topmanager.oiltycoon.social.dto.ErrorDto;
import com.topmanager.oiltycoon.social.dto.response.ErrorResponseDto;
import com.topmanager.oiltycoon.social.security.exception.ErrorCode;
import com.topmanager.oiltycoon.social.security.exception.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler implements AsyncUncaughtExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
        throw new RestException(ErrorCode.MAIL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {RestException.class})
    public ResponseEntity<?> handleRestException(RestException ex) {
        logger.debug("Handle rest exception: " + ex.getErrorCode().name());
        return ResponseEntity.badRequest().body(new ErrorDto(ex.getErrorCode().name(), ex.getMessage()));
    }

    @ExceptionHandler(value = {InvalidGrantException.class})
    public ResponseEntity<?> handleInvalidGrantException(InvalidGrantException ex) {
        logger.debug("Handle grant exception: " + ex.getOAuth2ErrorCode());
        return ResponseEntity.badRequest().body(new ErrorDto(ex.getOAuth2ErrorCode(), ex.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        BindingResult result = ex.getBindingResult();
        FieldError r = result.getFieldErrors().stream().findFirst().orElse(new FieldError("", "", ""));
        return ResponseEntity.badRequest().body(
                new ErrorDto(r.getCode() + ": " + r.getRejectedValue(), r.getDefaultMessage()));
    }


    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity.badRequest().body(new ErrorDto("MISSING_PATH_VARIABLE", ex.getMessage()));
    }
}
