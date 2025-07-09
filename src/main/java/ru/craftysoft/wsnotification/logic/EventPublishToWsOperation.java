package ru.craftysoft.wsnotification.logic;

import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.craftysoft.wsnotification.entrypoint.domain.Event;
import ru.craftysoft.wsnotification.service.CacheService;
import ru.craftysoft.wsnotification.service.WsEventPublishService;

import java.util.Set;

@ApplicationScoped
public class EventPublishToWsOperation {

    @Inject
    CacheService cacheService;
    @Inject
    WsEventPublishService wsEventPublishService;

    private static final Logger LOGGER = LoggerFactory.getLogger(EventPublishToWsOperation.class);

    public void process(Event event) {
        try {
            LOGGER.info("EventPublishToWsOperation.process.in event={}", event);
            Set<WebSocketConnection> connections = wsEventPublishService.publish(event);
            LOGGER.info("EventPublishToWsOperation.process.out connections={}", connections);
        } catch (Exception e) {
            LOGGER.error("EventPublishToRedisOperation.process.thrown", e);
        }
    }

}
