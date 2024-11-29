package pl.cezarysanecki.eventstore;

import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class EventStoreExtensions {

    private final EventStore eventStore;

    public EventStoreExtensions(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public <T> T aggregateStreamAsync(
            Supplier<T> createDefault,
            BiFunction<T, Object, T> evolve,
            UUID streamId,
            @Nullable Long atStreamVersion,
            @Nullable Instant atTimestamp
    ) {
        Collection<Object> events;
        try {
            events = eventStore.getEventsAsync(streamId, atStreamVersion, atTimestamp).get();
        } catch (Exception e) {
            throw new IllegalArgumentException("cannot retrieve events from stream " + streamId, e);
        }

        return events.stream()
                .reduce(
                        createDefault.get(),
                        evolve,
                        (aggregate1, aggregate2) -> aggregate1);
    }

    public <T> void handle(
            Supplier<T> createDefault,
            BiFunction<T, Object, T> evolve,
            BiFunction<Object, T, List<Object>> decide,
            UUID streamId,
            Object command,
            @Nullable Long expectedVersion
    ) {
        T entity = aggregateStreamAsync(
                createDefault,
                evolve,
                streamId,
                null,
                null
        );

        List<Object> events = decide.apply(command, entity);

        try {
            eventStore.appendEventsAsync(streamId, events, expectedVersion, events.getClass()).get();
        } catch (Exception e) {
            throw new IllegalArgumentException("cannot handle command " + command + " from stream " + streamId, e);
        }
    }

}
