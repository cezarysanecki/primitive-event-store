package pl.cezarysanecki.exampledomain;

import pl.cezarysanecki.exampledomain.events.Created;
import pl.cezarysanecki.exampledomain.events.EventA;
import pl.cezarysanecki.exampledomain.events.EventB;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EntityDecider {

    public static List<Object> handle(Entity entity, CommandA command) {
        if (!Objects.equals(entity.id, command.entityId)) {
            throw new IllegalArgumentException("trying to modify different entity");
        }
        if (entity.value + command.value < 0) {
            throw new IllegalArgumentException("value of entity cannot be negative");
        }

        return List.of(
                new EventA(entity.id, command.value, entity.version + 1)
        );
    }

    public static List<Object> handle(Entity entity, CommandB command) {
        if (!Objects.equals(entity.id, command.entityId)) {
            throw new IllegalArgumentException("trying to modify different entity");
        }
        if (entity.value + command.value.length() < 0) {
            throw new IllegalArgumentException("value of entity cannot be negative");
        }

        return List.of(
                new EventB(entity.id, command.value, entity.version + 1)
        );
    }

    public static List<Object> handle(Create command) {
        return List.of(
                new Created(command.entityId, 1L)
        );
    }

    public static List<Object> handle(Entity entity, Object command) {
        return switch (command) {
            case Create create -> handle(create);
            case CommandA commandA -> handle(entity, commandA);
            case CommandB commandB -> handle(entity, commandB);
            default -> throw new IllegalStateException("Unexpected value: " + command);
        };
    }

    public record Create(UUID entityId) {
    }

    public record CommandA(UUID entityId, int value) {
    }

    public record CommandB(UUID entityId, String value) {
    }

}
