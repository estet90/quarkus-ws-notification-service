package ru.craftysoft.wsnotification.util;

import io.quarkus.arc.Unremovable;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import jakarta.enterprise.context.ApplicationScoped;
import ru.craftysoft.wsnotification.entrypoint.domain.Event;

@ApplicationScoped
@Unremovable
public class EventDeserializer extends ObjectMapperDeserializer<Event> {
    public EventDeserializer() {
        super(Event.class);
    }
}
