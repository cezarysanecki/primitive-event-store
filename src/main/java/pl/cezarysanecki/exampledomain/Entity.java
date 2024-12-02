package pl.cezarysanecki.exampledomain;

import pl.cezarysanecki.exampledomain.events.EventA;
import pl.cezarysanecki.exampledomain.events.EventB;

import java.util.UUID;

public class Entity {

    final UUID id;
    final int value;
    final Long version;

    public Entity() {
        this.id = null;
        this.value = 0;
        this.version = 0L;
    }

    private Entity(UUID id, int value, Long version) {
        this.id = id;
        this.value = value;
        this.version = version;
    }

    public static Entity evolve(Entity entity, Object event) {
        return switch (event) {
            case EventA eventA -> entity.apply(eventA);
            case EventB eventB -> entity.apply(eventB);
            default -> throw new IllegalStateException("Unexpected value: " + event);
        };
    }

    private Entity apply(EventA event) {
        int value = this.value + event.value();
        return new Entity(this.id, value, event.version());
    }

    private Entity apply(EventB event) {
        int value = this.value + event.value().length();
        return new Entity(this.id, value, event.version());
    }

}
