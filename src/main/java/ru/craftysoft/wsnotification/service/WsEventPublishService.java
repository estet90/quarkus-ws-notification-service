package ru.craftysoft.wsnotification.service;

import io.quarkus.websockets.next.Connection;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.craftysoft.wsnotification.entrypoint.domain.Event;

import java.util.List;
import java.util.Set;

@ApplicationScoped
public class WsEventPublishService {

    @Inject
    CacheService cacheService;

    private static final Logger LOGGER = LoggerFactory.getLogger(WsEventPublishService.class);

    public Set<WebSocketConnection> publish(Event event) {
        Set<WebSocketConnection> connections = cacheService.getConnectionsByKey(event.key());
        connections.forEach(connection -> connection.sendText(event.text())
                .subscribe()
                .with(io.smallrye.mutiny.vertx.UniHelper.NOOP));
        List<String> connectionIds = connections.stream()
                .map(Connection::id)
                .toList();
        LOGGER.info("WsEventPublishService.publish\nconnections={}\nevent={}", connectionIds, event);
        return connections;
    }

}
