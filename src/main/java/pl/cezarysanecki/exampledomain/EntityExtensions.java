package pl.cezarysanecki.exampledomain;

import org.springframework.lang.Nullable;
import pl.cezarysanecki.eventstore.EventStoreExtensions;

import java.time.Instant;
import java.util.UUID;

public class EntityExtensions {

    private final EventStoreExtensions eventStoreExtensions;

    public EntityExtensions(EventStoreExtensions eventStoreExtensions) {
        this.eventStoreExtensions = eventStoreExtensions;
    }

    public Entity getEntity(
            UUID entityId,
            @Nullable Long atStreamVersion,
            @Nullable Instant atTimestamp
    ) {
        return eventStoreExtensions.aggregateStreamAsync(
                Entity::new,
                Entity::evolve,
                entityId,
                atStreamVersion,
                atTimestamp
        );
    }

    public void handle(
            UUID entityId,
            Object command,
            @Nullable Long expectedVersion
    ) {
        eventStoreExtensions.handle(
                Entity::new,
                Entity::evolve,
                (handlingCommand, entity) -> EntityDecider.handle(entity, handlingCommand),
                entityId,
                command,
                expectedVersion
        );
    }

}
