package pl.cezarysanecki.exampledomain.events;

import java.util.UUID;

public record EventB(UUID entityId, String value, Long version) {
}
