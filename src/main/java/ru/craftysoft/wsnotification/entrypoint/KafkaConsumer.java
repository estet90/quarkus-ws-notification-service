package ru.craftysoft.wsnotification.entrypoint;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import ru.craftysoft.wsnotification.entrypoint.domain.Event;
import ru.craftysoft.wsnotification.logic.EventPublishToRedisOperation;

@ApplicationScoped
public class KafkaConsumer {

    @Inject
    EventPublishToRedisOperation eventPublishToRedisOperation;

    @Incoming("event-handle")
    public Uni<Void> handle(Event event) {
        return eventPublishToRedisOperation.process(event);
    }

}
