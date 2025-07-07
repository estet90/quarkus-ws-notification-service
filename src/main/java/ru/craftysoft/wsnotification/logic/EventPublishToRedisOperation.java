package ru.craftysoft.wsnotification.logic;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.craftysoft.wsnotification.entrypoint.domain.Event;
import ru.craftysoft.wsnotification.integration.RedisClient;
import ru.craftysoft.wsnotification.service.CacheService;
import ru.craftysoft.wsnotification.service.WsEventPublishService;
import ru.craftysoft.wsnotification.util.InstanceGuidHolder;

@ApplicationScoped
public class EventPublishToRedisOperation {

    @Inject
    RedisClient redisClient;
    @Inject
    InstanceGuidHolder instanceGuidHolder;
    @Inject
    CacheService cacheService;
    @Inject
    WsEventPublishService wsEventPublishService;

    private static final Logger LOGGER = LoggerFactory.getLogger(EventPublishToRedisOperation.class);

    public Uni<Void> process(Event event) {
        LOGGER.info("EventPublishToRedisOperation.process.in event={}", event);
        return redisClient.getChannelsByKey(event.key())
                .invoke(channels -> {
                    if (channels.isEmpty()) {
                        return;
                    }
                    boolean hasCurrentChannel = channels.remove(instanceGuidHolder.getInstanceGuid());
                    if (hasCurrentChannel) {
                        wsEventPublishService.publish(event);
                    }
                    channels.forEach(channel -> redisClient.publish(event, channel));
                    LOGGER.info("EventPublishToRedisOperation.process.out channels={}", channels);
                })
                .onFailure()
                .invoke(throwable -> LOGGER.error("EventPublishToRedisOperation.process.thrown", throwable))
                .replaceWithVoid();
    }
}
