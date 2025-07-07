package ru.craftysoft.wsnotification.logic;

import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.craftysoft.wsnotification.entrypoint.domain.WsRequest;
import ru.craftysoft.wsnotification.integration.RedisClient;
import ru.craftysoft.wsnotification.service.CacheService;
import ru.craftysoft.wsnotification.util.InstanceGuidHolder;

import java.util.Set;

@ApplicationScoped
public class WsRequestHandleOperation {

    @Inject
    CacheService cacheService;
    @Inject
    RedisClient redisClient;
    @Inject
    InstanceGuidHolder instanceGuidHolder;

    private static final Logger LOGGER = LoggerFactory.getLogger(WsRequestHandleOperation.class);

    public void process(WebSocketConnection connection, WsRequest wsRequest) {
        LOGGER.info("WsRequestHandleOperation.process.in\nconnection={}\nwsRequest={}", connection, wsRequest);
        final String channel = instanceGuidHolder.getInstanceGuid();
        final String key = wsRequest.key();
        switch (wsRequest.type()) {
            case SUBSCRIBE -> {
                cacheService.addData(connection, key);
                redisClient.addChannelToKey(key, channel);
            }
            case UNSUBSCRIBE -> {
                cacheService.removeData(connection, key);
                Set<WebSocketConnection> connectionsByKey = cacheService.getConnectionsByKey(key);
                if (connectionsByKey.isEmpty()) {
                    redisClient.removeChannelByKey(key, channel);
                }
            }
        }
        LOGGER.info("WsRequestHandleOperation.process.out");
    }

}
