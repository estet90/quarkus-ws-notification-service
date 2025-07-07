package ru.craftysoft.wsnotification.entrypoint;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.craftysoft.wsnotification.entrypoint.domain.Event;
import ru.craftysoft.wsnotification.logic.EventPublishToWsOperation;
import ru.craftysoft.wsnotification.util.InstanceGuidHolder;

public class RedisSubscriber {

    @Inject
    InstanceGuidHolder instanceGuidHolder;
    @Inject
    RedisDataSource reactiveRedisDataSource;
    @Inject
    EventPublishToWsOperation eventPublishToWsOperation;

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSubscriber.class);

    void onStart(@Observes StartupEvent startupEvent) {
        String channel = instanceGuidHolder.getInstanceGuid();
        LOGGER.info("RedisSubscriber.onStart channel={}", channel);
        PubSubCommands.RedisSubscriber ignored = reactiveRedisDataSource.pubsub(Event.class)
                .subscribe(channel, eventPublishToWsOperation::process);
    }
}
