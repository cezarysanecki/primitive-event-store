package pl.cezarysanecki.exampledomain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.cezarysanecki.eventstore.EventStoreExtensions;

@Configuration(proxyBeanMethods = false)
public class EntityConfig {

    private final EventStoreExtensions eventStoreExtensions;

    public EntityConfig(EventStoreExtensions eventStoreExtensions) {
        this.eventStoreExtensions = eventStoreExtensions;
    }

    @Bean
    public EntityExtensions entityExtensions() {
        return new EntityExtensions(eventStoreExtensions);
    }

}
