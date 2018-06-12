package com.topmanager.oiltycoon;


import com.topmanager.oiltycoon.dto.ErrorDto;
import com.topmanager.oiltycoon.dto.response.ErrorResponseDto;
import com.topmanager.oiltycoon.security.exception.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(value = RestException.class)
    public ResponseEntity<?> handleRestException(RestException ex) {
        logger.error("Handle rest exception!!!!!!!!");
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
