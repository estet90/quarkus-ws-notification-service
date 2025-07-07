package ru.craftysoft.wsnotification.service;

import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.craftysoft.wsnotification.integration.RedisClient;
import ru.craftysoft.wsnotification.util.InstanceGuidHolder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class CacheService implements WsCloseHandler {

    @Inject
    RedisClient redisClient;
    @Inject
    InstanceGuidHolder instanceGuidHolder;

    private final Map<String, Set<WebSocketConnection>> connectionsByKey = new ConcurrentHashMap<>();
    private final Map<WebSocketConnection, Set<String>> keysByConnection = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheService.class);

    public void addData(WebSocketConnection wsConnection, String key) {
        connectionsByKey.compute(key, (k, connections) -> {
            if (connections == null) {
                connections = new HashSet<>();
            }
            connections.add(wsConnection);
            return connections;
        });
        keysByConnection.compute(wsConnection, (connection, keys) -> {
            if (keys == null) {
                keys = new HashSet<>();
            }
            keys.add(key);
            return keys;
        });
    }

    public void removeData(WebSocketConnection wsConnection, String key) {
        removeConnection(wsConnection, key);
        Set<String> keys = keysByConnection.get(wsConnection);
        if (keys != null) {
            keys.remove(key);
            if (keys.isEmpty()) {
                keysByConnection.remove(wsConnection);
            }
        }
    }

    public Set<WebSocketConnection> getConnectionsByKey(String key) {
        return connectionsByKey.getOrDefault(key, Set.of());
    }

    public Set<String> getKeys() {
        return connectionsByKey.keySet();
    }

    @Override
    public void onClose(WebSocketConnection wsConnection) {
        Set<String> keys = keysByConnection.remove(wsConnection);
        LOGGER.info("CacheService.onClose\nconnection={}\nkeys={}", wsConnection.id(), keys);
        if (keys != null) {
            keys.iterator().forEachRemaining(key -> removeConnection(wsConnection, key));
        }
    }

    private void removeConnection(WebSocketConnection wsConnection, String key) {
        Set<WebSocketConnection> connections = connectionsByKey.get(key);
        if (connections != null) {
            connections.remove(wsConnection);
            if (connections.isEmpty()) {
                connectionsByKey.remove(key);
                String channel = instanceGuidHolder.getInstanceGuid();
                redisClient.removeChannelByKey(key, channel);
                LOGGER.info("CacheService.onClose.clearRedisCache\nkey={}\nchannel={}", key, channel);
            }
        }
    }
}
