package ru.craftysoft.wsnotification.entrypoint;

import io.quarkus.arc.All;
import io.quarkus.runtime.Startup;
import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.inject.Inject;
import ru.craftysoft.wsnotification.entrypoint.domain.WsRequest;
import ru.craftysoft.wsnotification.logic.WsRequestHandleOperation;
import ru.craftysoft.wsnotification.service.WsCloseHandler;

import java.util.List;

@Startup
@WebSocket(path = "/ws/notification")
public class WsController {

    @Inject
    WsRequestHandleOperation wsRequestHandleOperation;

    @All
    List<WsCloseHandler> closeHandlers;

    @OnTextMessage
    public void onMessage(WebSocketConnection connection, WsRequest wsRequest) {
        wsRequestHandleOperation.process(connection, wsRequest);
    }

    @OnClose
    public void onClose(WebSocketConnection connection) {
        closeHandlers.forEach(handler -> handler.onClose(connection));
    }

}
