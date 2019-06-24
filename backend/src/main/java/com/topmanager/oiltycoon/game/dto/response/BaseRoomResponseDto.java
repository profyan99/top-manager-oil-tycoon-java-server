package com.topmanager.oiltycoon.game.dto.response;

public abstract class BaseRoomResponseDto<T> {
    protected ResponseObjectType objectType;
    protected ResponseEventType eventType;
    protected T body;

    public BaseRoomResponseDto(ResponseObjectType objectType, ResponseEventType eventType, T body) {
        this.objectType = objectType;
        this.eventType = eventType;
        this.body = body;
    }

    public ResponseObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ResponseObjectType objectType) {
        this.objectType = objectType;
    }

    public ResponseEventType getEventType() {
        return eventType;
    }

    public void setEventType(ResponseEventType eventType) {
        this.eventType = eventType;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
