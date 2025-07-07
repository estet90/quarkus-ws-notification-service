package ru.craftysoft.wsnotification.logic;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.craftysoft.wsnotification.entrypoint.domain.Event;
import ru.craftysoft.wsnotification.integration.RedisClient;
import ru.craftysoft.wsnotification.service.WsEventPublishService;
import ru.craftysoft.wsnotification.util.InstanceGuidHolder;

@ApplicationScoped
public class EventPublishToRedisOperation {

    @Inject
    RedisClient redisClient;
    @Inject
    InstanceGuidHolder instanceGuidHolder;
    @Inject
    WsEventPublishService wsEventPublishService;

    private static final Logger LOGGER = LoggerFactory.getLogger(EventPublishToRedisOperation.class);

    public Uni<Void> process(Event event) {
        LOGGER.info("EventPublishToRedisOperation.process.in event={}", event);
        return redisClient.getChannelsByKey(event.key())
                .flatMap(channels -> {
                    if (channels.isEmpty()) {
                        return Uni.createFrom().voidItem();
                    }
                    boolean hasCurrentChannel = channels.remove(instanceGuidHolder.getInstanceGuid());
                    if (hasCurrentChannel) {
                        wsEventPublishService.publish(event);
                        LOGGER.info("EventPublishToRedisOperation.process.out publish local channel={}", instanceGuidHolder.getInstanceGuid());
                    }
                    if (channels.isEmpty()) {
                        return Uni.createFrom().voidItem();
                    }
                    return Multi.createFrom().iterable(channels)
                            .call(channel -> redisClient.publish(event, channel))
                            .toUni()
                            .invoke(() -> LOGGER.info("EventPublishToRedisOperation.process.out channels={}", channels))
                            .replaceWithVoid();
                })
                .onFailure()
                .invoke(throwable -> LOGGER.error("EventPublishToRedisOperation.process.thrown", throwable))
                .replaceWithVoid();
    }
}
