package com.topmanager.oiltycoon.security.exception;

public enum ErrorCode {
    ERROR_WITH_DAtABASE("Error with data base"),
    ERROR_WITH_AUTHENTICATION("Error with authentication"),
    AUTHENTICATION_ERROR("You need to authorize"),
    ACCOUNT_NOT_FOUND("Account not found");


    private String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
