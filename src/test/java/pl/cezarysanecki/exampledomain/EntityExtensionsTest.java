package pl.cezarysanecki.exampledomain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.cezarysanecki.eventstore.EventStoreConfig;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(
        classes = {
                EventStoreConfig.class,
                EntityConfig.class
        }
)
class EntityExtensionsTest {

    @Autowired
    EntityExtensions entityExtensions;

    @Test
    public void checkIfEntityExtensionsIsWorking() {
        UUID entityId = UUID.randomUUID();

        entityExtensions.handle(
                entityId,
                new EntityDecider.CommandA(entityId, 23),
                null
        );
        entityExtensions.handle(
                entityId,
                new EntityDecider.CommandB(entityId, "22"),
                null
        );

        Entity entity = entityExtensions.getEntity(
                entityId,
                null,
                null);
        assertThat(entity.value).isEqualTo(25);

    }

}