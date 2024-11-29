package pl.cezarysanecki.exampledomain.events;

import java.util.UUID;

public record EventA(UUID entityId, int value, Long version) {
}
