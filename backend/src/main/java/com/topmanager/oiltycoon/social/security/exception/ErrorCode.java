package com.topmanager.oiltycoon.social.security.exception;

public enum ErrorCode {
    ERROR_WITH_DATABASE("Неизвестная ошибка."),
    MAIL_SERVER_ERROR("Ошибка отправки электронного письма."),
    ERROR_WITH_AUTHENTICATION("Ошибка авторизации."),
    AUTHENTICATION_ERROR("Вы должны авторизоваться."),
    AUTHORIZATION_ERROR("Доступ запрещен."),
    NOT_FOUND("Ресурс не найден."),
    PASSWORDS_IS_NOT_EQUALS("Пароли должны совпадать."),
    ACCOUNT_NOT_FOUND("Аккаунт не найден."),
    EMAIL_NOT_UNIQUE("Пользователь с таким электронным адресом уже существует."),
    USERNAME_NOT_UNIQUE("Никнейм должен быть уникальным."),
    VERIFICATION_TOKEN_NOT_FOUND("Неверный токен."),
    CONFIRM_TIME_EXPIRED("Время подтверждения истекло."),
    INVALID_OLD_PASSWORD("Неверный старый пароль.");


    private String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
