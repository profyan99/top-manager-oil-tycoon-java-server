package com.topmanager.oiltycoon.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class RestException extends RuntimeException {
    private ErrorCode errorCode;

    public RestException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public RestException(ErrorCode errorCode) {
        this(errorCode.getMessage(), errorCode);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
