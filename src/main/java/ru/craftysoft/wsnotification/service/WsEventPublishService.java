package ru.craftysoft.wsnotification.service;

import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.craftysoft.wsnotification.entrypoint.domain.Event;

import java.util.Set;

@ApplicationScoped
public class WsEventPublishService {

    @Inject
    CacheService cacheService;

    public void publish(Event event) {
        Set<WebSocketConnection> connections = cacheService.getConnectionsByKey(event.key());
        publish(event, connections);
    }

    public void publish(Event event, Set<WebSocketConnection> connections) {
        connections.forEach(connection -> connection.sendText(event.text())
                .subscribe()
                .with(io.smallrye.mutiny.vertx.UniHelper.NOOP));
    }

}
