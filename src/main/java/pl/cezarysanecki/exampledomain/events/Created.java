package pl.cezarysanecki.exampledomain.events;

import java.util.UUID;

public record Created(UUID entityId, Long version) {
}
