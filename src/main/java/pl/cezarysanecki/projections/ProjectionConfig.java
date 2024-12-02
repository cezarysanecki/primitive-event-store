package pl.cezarysanecki.projections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration(proxyBeanMethods = false)
public class ProjectionConfig {

    @Bean
    LongDashboardProjection longDashboardProjection(JdbcTemplate jdbcTemplate) {
        return new LongDashboardProjection(jdbcTemplate);
    }

    @Bean
    StringDashboardProjection stringDashboardProjection(JdbcTemplate jdbcTemplate) {
        return new StringDashboardProjection(jdbcTemplate);
    }

}
