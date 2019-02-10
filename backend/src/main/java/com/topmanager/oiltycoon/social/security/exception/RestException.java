package com.topmanager.oiltycoon.social.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class RestException extends OAuth2Exception {
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
