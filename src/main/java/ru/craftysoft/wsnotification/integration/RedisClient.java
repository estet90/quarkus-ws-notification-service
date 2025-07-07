package ru.craftysoft.wsnotification.integration;

import io.quarkus.redis.datasource.RedisDataSource;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ru.craftysoft.wsnotification.entrypoint.domain.Event;

import java.util.Set;

@ApplicationScoped
public class RedisClient {

    @Inject
    RedisDataSource reactiveRedisDataSource;

    public Uni<Void> publish(Event event, String channel) {
        return reactiveRedisDataSource.getReactive().pubsub(Event.class).publish(channel, event);
    }

    public Uni<Set<String>> getChannelsByKey(String key) {
        return reactiveRedisDataSource.getReactive().set(String.class).smembers("ws:" + key);
    }

    public void addChannelToKey(String key, String channel) {
        reactiveRedisDataSource.set(String.class).sadd("ws:" + key, channel);
    }

    public void removeChannelByKey(String key, String channel) {
        reactiveRedisDataSource.set(String.class).srem("ws:" + key, channel);
    }

}
