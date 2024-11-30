package pl.cezarysanecki.exampledomain;

import pl.cezarysanecki.exampledomain.events.EventA;
import pl.cezarysanecki.exampledomain.events.EventB;

import java.util.List;
import java.util.UUID;

public class EntityDecider {

    public static List<Object> handle(Entity entity, CommandA command) {
        if (entity.value + command.value < 0) {
            throw new IllegalArgumentException("value of entity cannot be negative");
        }

        return List.of(
                new EventA(entity.id, command.value, entity.version + 1)
        );
    }

    public static List<Object> handle(Entity entity, CommandB command) {
        if (entity.value + command.value.length() < 0) {
            throw new IllegalArgumentException("value of entity cannot be negative");
        }

        return List.of(
                new EventB(entity.id, command.value, entity.version + 1)
        );
    }

    public static List<Object> handle(Entity entity, Object command) {
        return switch (command) {
            case CommandA commandA -> handle(entity, commandA);
            case CommandB commandB -> handle(entity, commandB);
            default -> throw new IllegalStateException("Unexpected value: " + command);
        };
    }

    public record CommandA(UUID entityId, int value) {
    }

    public record CommandB(UUID entityId, String value) {
    }

}
