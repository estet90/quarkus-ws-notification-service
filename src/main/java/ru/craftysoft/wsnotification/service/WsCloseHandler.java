package ru.craftysoft.wsnotification.service;

import io.quarkus.websockets.next.WebSocketConnection;

public interface WsCloseHandler {

    void onClose(WebSocketConnection connection);

}
