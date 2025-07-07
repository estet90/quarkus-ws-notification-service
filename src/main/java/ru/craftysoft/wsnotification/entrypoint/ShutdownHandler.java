package ru.craftysoft.wsnotification.entrypoint;

import io.quarkus.runtime.ShutdownEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.craftysoft.wsnotification.integration.RedisClient;
import ru.craftysoft.wsnotification.service.CacheService;
import ru.craftysoft.wsnotification.util.InstanceGuidHolder;

import java.util.Set;

@ApplicationScoped
public class ShutdownHandler {

    @Inject
    CacheService cacheService;
    @Inject
    RedisClient redisClient;
    @Inject
    InstanceGuidHolder instanceGuidHolder;

    private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownHandler.class);

    void onShutdown(@Observes ShutdownEvent event) {
        String channel = instanceGuidHolder.getInstanceGuid();
        Set<String> keys = cacheService.getKeys();
        for (String key : keys) {
            redisClient.removeChannelByKey(key, channel);
        }
        LOGGER.info("ShutdownHandler.onShutdown\nchannel={}\nkeys={}", channel, keys);
    }

}
