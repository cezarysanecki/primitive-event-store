package pl.cezarysanecki.projections;

import org.springframework.jdbc.core.JdbcTemplate;
import pl.cezarysanecki.exampledomain.events.Created;
import pl.cezarysanecki.exampledomain.events.EventA;

public class LongDashboard {
    public Long sum;

    public void add(Long value) {
        sum += value;
    }
}

class LongDashboardProjection extends AbstractProjection {

    private final JdbcTemplate jdbcTemplate;

    public LongDashboardProjection(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        projects(EventA.class, this::apply);
        projects(Created.class, this::apply);
    }

    private void apply(EventA event) {
        jdbcTemplate.update(
                "UPDATE long_projection SET value = value + ? WHERE id = ?::uuid",
                event.value(),
                event.entityId()
        );
    }

    private void apply(Created event) {
        jdbcTemplate.update(
                "INSERT INTO long_projection VALUES (?::uuid, ?)",
                event.entityId(),
                0
        );
    }

}
