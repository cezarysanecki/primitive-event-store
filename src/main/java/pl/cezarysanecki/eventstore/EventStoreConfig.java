package pl.cezarysanecki.eventstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import pl.cezarysanecki.projections.Projection;

import javax.sql.DataSource;
import java.util.List;

@Configuration(proxyBeanMethods = false)
public class EventStoreConfig {

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/eventstore");
        dataSource.setUsername("postgres");
        dataSource.setPassword("root");
        dataSource.setDriverClassName("org.postgresql.Driver");

        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(5);
        dataSource.setIdleTimeout(30000);
        dataSource.setMaxLifetime(1800000);
        dataSource.setConnectionTimeout(30000);

        return dataSource;
    }

    @Bean
    public TransactionTemplate transactionTemplate(DataSource dataSource) {
        PlatformTransactionManager transactionManager = new JdbcTransactionManager(dataSource);
        return new TransactionTemplate(transactionManager);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public EventStore eventStore(
            TransactionTemplate transactionTemplate,
            JdbcTemplate jdbcTemplate,
            ObjectMapper objectMapper,
            List<Projection> projections
    ) {
        EventStore eventStore = new EventStore(transactionTemplate, jdbcTemplate, objectMapper);
        projections.forEach(eventStore::registerProjection);
        return eventStore;
    }

    @Bean
    public EventStoreExtensions eventStoreExtension(
            EventStore eventStore
    ) {
        return new EventStoreExtensions(eventStore);
    }
}
