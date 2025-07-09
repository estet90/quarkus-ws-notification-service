package ru.craftysoft.wsnotification.logic;

import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.craftysoft.wsnotification.entrypoint.domain.WsRequest;
import ru.craftysoft.wsnotification.service.CacheService;

@ApplicationScoped
public class WsRequestHandleOperation {

    @Inject
    CacheService cacheService;

    private static final Logger LOGGER = LoggerFactory.getLogger(WsRequestHandleOperation.class);

    public void process(WebSocketConnection connection, WsRequest wsRequest) {
        try {
            LOGGER.info("WsRequestHandleOperation.process.in\nconnection={}\nwsRequest={}", connection.id(), wsRequest);
            final String key = wsRequest.key();
            switch (wsRequest.type()) {
                case SUBSCRIBE -> cacheService.addData(connection, key);
                case UNSUBSCRIBE -> cacheService.removeData(connection, key);
            }
            LOGGER.info("WsRequestHandleOperation.process.out");
        } catch (RuntimeException e) {
            LOGGER.error("WsRequestHandleOperation.process.out", e);
            throw e;
        }
    }

}
