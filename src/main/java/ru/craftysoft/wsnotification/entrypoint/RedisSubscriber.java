package ru.craftysoft.wsnotification.entrypoint;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.craftysoft.wsnotification.entrypoint.domain.Event;
import ru.craftysoft.wsnotification.logic.EventPublishToWsOperation;
import ru.craftysoft.wsnotification.util.InstanceGuidHolder;

@Startup
@ApplicationScoped
public class RedisSubscriber {

    private final InstanceGuidHolder instanceGuidHolder;
    private final PubSubCommands.RedisSubscriber redisSubscriber;

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSubscriber.class);

    public RedisSubscriber(InstanceGuidHolder instanceGuidHolder,
                           RedisDataSource reactiveRedisDataSource,
                           EventPublishToWsOperation eventPublishToWsOperation) {
        this.instanceGuidHolder = instanceGuidHolder;
        String channel = instanceGuidHolder.getInstanceGuid();
        this.redisSubscriber = reactiveRedisDataSource.pubsub(Event.class)
                .subscribe(channel, eventPublishToWsOperation::process);
        LOGGER.info("RedisSubscriber.onStart channel={}", channel);
    }

    void onShutdown(@Observes ShutdownEvent event) {
        String channel = instanceGuidHolder.getInstanceGuid();
        this.redisSubscriber.unsubscribe();
        LOGGER.info("RedisSubscriber.onShutdown channel={}", channel);
    }
}
