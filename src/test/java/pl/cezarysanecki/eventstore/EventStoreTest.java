package pl.cezarysanecki.eventstore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.cezarysanecki.exampledomain.events.EventA;
import pl.cezarysanecki.exampledomain.events.EventB;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(
        classes = {EventStoreConfig.class}
)
class EventStoreTest {

    @Autowired
    private EventStore eventStore;

    @Test
    public void checkIfEventStoreIsWorking() throws ExecutionException, InterruptedException {
        //given
        UUID entityId = UUID.randomUUID();
        UUID streamId = UUID.randomUUID();

        //when
        eventStore.appendEventsAsync(
                streamId,
                List.of(
                        new EventA(entityId, 24, 1L),
                        new EventB(entityId, "test-123", 2L)
                ),
                null,
                String.class
        ).get();

        //then
        Collection<Object> events = eventStore.getEventsAsync(streamId, null, null).get();
        assertThat(events).anySatisfy(event -> {
            assertThat(event)
                    .isInstanceOf(EventA.class)
                    .isEqualTo(new EventA(entityId, 24, 1L));
        });
        assertThat(events).anySatisfy(event -> {
            assertThat(event)
                    .isInstanceOf(EventB.class)
                    .isEqualTo(new EventB(entityId, "test-123", 2L));
        });
    }

}