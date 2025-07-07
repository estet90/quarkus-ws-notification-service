package ru.craftysoft.wsnotification.service;

import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class CacheService implements WsCloseHandler {

    private final Map<String, Set<WebSocketConnection>> connectionsByKey = new ConcurrentHashMap<>();
    private final Map<WebSocketConnection, Set<String>> keysByConnection = new ConcurrentHashMap<>();

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
        Set<WebSocketConnection> connections = connectionsByKey.get(key);
        if (connections != null) {
            connections.remove(wsConnection);
            if (connections.isEmpty()) {
                connectionsByKey.remove(key);
            }
        }
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

    public Set<String> getKeysByConnection(WebSocketConnection connection) {
        return keysByConnection.getOrDefault(connection, Set.of());
    }

    public Set<String> getKeys() {
        return connectionsByKey.keySet();
    }

    @Override
    public void onClose(WebSocketConnection connection) {
        Set<String> keys = keysByConnection.remove(connection);
        if (keys != null) {
            keys.iterator().forEachRemaining(key -> {
                Set<WebSocketConnection> connections = connectionsByKey.get(key);
                if (connections != null) {
                    connections.remove(connection);
                    if (connections.isEmpty()) {
                        connectionsByKey.remove(key);
                    }
                }
            });
        }
    }
}
