package pl.cezarysanecki.eventstore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

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
        UUID streamId = UUID.randomUUID();

        //when
        eventStore.appendEventsAsync(
                streamId,
                List.of(
                        new FirstExampleEvent(24),
                        new SecondExampleEvent("test-123")
                ),
                null,
                String.class
        ).get();

        //then
        Collection<Object> events = eventStore.getEventsAsync(streamId, null, null).get();
        assertThat(events).anySatisfy(event -> {
            assertThat(event)
                    .isInstanceOf(FirstExampleEvent.class)
                    .isEqualTo(new FirstExampleEvent(24));
        });
        assertThat(events).anySatisfy(event -> {
            assertThat(event)
                    .isInstanceOf(SecondExampleEvent.class)
                    .isEqualTo(new SecondExampleEvent("test-123"));
        });
    }

}

record FirstExampleEvent(int value) {
}

record SecondExampleEvent(String value) {
}