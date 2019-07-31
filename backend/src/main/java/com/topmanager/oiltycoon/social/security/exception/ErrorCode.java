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
    INVALID_OLD_PASSWORD("Неверный старый пароль."),
    ACCESS_TOKEN_EXPIRED("Истекло время access token. Воспользуйтесь refresh token."),

    //game
    ROOM_NAME_NOT_UNIQUE("Комната с таким названием уже существует."),
    ROOM_NOT_FOUND("Неверный id комнаты. Комната не найдена."),
    ROOM_IS_FULL("В комнате больше нет свободных мест."),
    GAME_HAS_ALREADY_STARTED("Игра уже началась"),
    INVALID_ROOM_PASSWORD("Неверный пароль от комнаты"),
    PLAYER_NOT_SATISFY("Не достаточно прав для игры в этой комнате."),
    INVALID_ROOM_ID("Неверный номер комнаты."),
    ALREADY_IN_ANOTHER_ROOM("Вы уже играете в другой комнате."),
    UNDEFINED_ERROR("Неизвестная ошибка"),

    FACTORY_ALREADY_EXISTS("Такой НПЗ уже есть в компании."),
    GAS_STATION_ALREADY_EXISTS("Такая АЗС уже есть в компании."),
    OIL_WELL_ALREADY_EXISTS("Такая скважина уже есть в компании."),
    COMPANY_NAME_ALREADY_EXISTS("Компания с таким названием уже зарегистрирована."),
    INVALID_PRODUCT_PRICE("Неверная цена товара.");


    private String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
