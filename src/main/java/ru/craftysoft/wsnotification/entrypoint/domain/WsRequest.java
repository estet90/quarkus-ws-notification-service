package ru.craftysoft.wsnotification.entrypoint.domain;

public record WsRequest(String key, Type type) {
    public enum Type {
        SUBSCRIBE,
        UNSUBSCRIBE
    }
}
