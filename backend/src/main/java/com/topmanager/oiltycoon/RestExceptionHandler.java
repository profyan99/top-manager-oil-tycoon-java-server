package com.topmanager.oiltycoon;


import com.topmanager.oiltycoon.dto.ErrorDto;
import com.topmanager.oiltycoon.dto.response.ErrorResponseDto;
import com.topmanager.oiltycoon.security.exception.ErrorCode;
import com.topmanager.oiltycoon.security.exception.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ControllerAdvice//(basePackages = {"com.topmanager.oiltycoon"})
@RequestMapping(produces = "application/json")
@ResponseBody
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @PostConstruct
    public void construct() {
        logger.error("Post Construct RestExceptionHandler");
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
                                                                   HttpStatus status, WebRequest request) {
        return ResponseEntity.badRequest().body(
                new ArrayList<>(Collections.singletonList(new ErrorDto(
                        ErrorCode.NOT_FOUND.name(),
                        ErrorCode.NOT_FOUND.getMessage()))));
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<?> handleAccessDeniedException(Exception ex, WebRequest request) {
        logger.debug("Handle access denied exception");
        return ResponseEntity.badRequest().body(
                new ArrayList<>(Collections.singletonList(new ErrorDto(
                        ErrorCode.AUTHENTICATION_ERROR.name(),
                        ErrorCode.AUTHENTICATION_ERROR.getMessage()))));
    }

    @ExceptionHandler(value = RestException.class)
    public ResponseEntity<?> handleRestException(RestException ex) {
        logger.debug("Handle rest exception: "+ex.getErrorCode().name());
        List<ErrorDto> errors = new ArrayList<>();
        errors.add(new ErrorDto(ex.getErrorCode().name(), ex.getMessage()));
        return ResponseEntity.badRequest().body(new ErrorResponseDto(errors));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        BindingResult result = ex.getBindingResult();
        List<ErrorDto> errors = new ArrayList<>();
        result.getFieldErrors().forEach((e) ->
                errors.add(new ErrorDto(e.getCode() + ": " + e.getRejectedValue(), e.getDefaultMessage())));
        return ResponseEntity.badRequest().body(new ErrorResponseDto(errors));
    }

}
