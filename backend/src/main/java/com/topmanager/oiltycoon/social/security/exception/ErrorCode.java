package com.topmanager.oiltycoon.social.security.exception;

public enum ErrorCode {
    ERROR_WITH_DAtABASE("Error with data base"),
    ERROR_WITH_AUTHENTICATION("Error with authentication"),
    AUTHENTICATION_ERROR("You need to authorize"),
    AUTHORIZATION_ERROR("Access denied"),
    NOT_FOUND("Resource not found"),
    ACCOUNT_NOT_FOUND("Account not found"),
    EMAIL_NOT_UNIQUE("User with this email address has already been registered"),
    USERNAME_NOT_UNIQUE("Username should be unique"),
    VERIFICATION_TOKEN_NOT_FOUND("Invalid verification token"),
    CONFIRM_TIME_EXPIRED("Verification timeout expired"),
    INVALID_OLD_PASSWORD("Invalid old password");


    private String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
