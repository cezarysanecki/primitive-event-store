package pl.cezarysanecki.eventstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventStore {

    private final TransactionTemplate transactionTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public EventStore(TransactionTemplate transactionTemplate, JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.transactionTemplate = transactionTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public Future<Collection<Object>> getEventsAsync(
            UUID streamId,
            @Nullable Long atStreamVersion,
            @Nullable Instant atTimestamp
    ) {
        return CompletableFuture.supplyAsync(() -> {
            String atStreamVersionCondition = atStreamVersion != null ? "AND version <= ? " : "";
            String atTimestampCondition = atTimestamp != null ? "AND created <= ? " : "";

            List<Object> params = Stream.of(
                            streamId,
                            atStreamVersion,
                            atTimestamp)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableList());

            List<Map<String, Object>> events = jdbcTemplate.queryForList(
                    "SELECT id, data, stream_id, type, version, created " +
                            "FROM events " +
                            "WHERE stream_id = ? " +
                            atStreamVersionCondition +
                            atTimestampCondition +
                            "ORDER BY version",
                    params.toArray(new Object[0])
            );

            return events.stream()
                    .map(event -> {
                        try {
                            return objectMapper.readValue(
                                    event.get("data").toString(),
                                    Class.forName((String) event.get("type"))
                            );
                        } catch (Exception e) {
                            throw new IllegalArgumentException("cannot deserialize event " + event, e);
                        }
                    })
                    .collect(Collectors.toUnmodifiableList());
        });
    }

    public <StreamType> Future<Void> appendEventsAsync(
            UUID streamId,
            Collection<Object> events,
            @Nullable Long expectedVersion,
            StreamType streamType
    ) {
        return CompletableFuture.runAsync(() -> {
            transactionTemplate.executeWithoutResult((transactionStatus) -> {
                AtomicLong version = new AtomicLong(expectedVersion == null ? 0 : expectedVersion);

                events.forEach(event -> {
                    try {
                        Boolean result = jdbcTemplate.queryForObject(
                                "SELECT append_event(?::uuid, ?::jsonb, ?, ?::uuid, ?, ?)",
                                Boolean.class,
                                UUID.randomUUID(),
                                objectMapper.writeValueAsString(event),
                                event.getClass().getName(),
                                streamId,
                                streamType.getClass().getName(),
                                expectedVersion
                        );

                        if (expectedVersion == null) {
                            version.set(0);
                        }
                        version.incrementAndGet();

                        if (Boolean.FALSE.equals(result))
                            throw new IllegalArgumentException("expected version did not match the stream version!");
                    } catch (JsonProcessingException e) {
                        throw new IllegalArgumentException("cannot serialize event " + event, e);
                    }
                });
            });
        });
    }

}
