package pl.cezarysanecki.projections;

import org.springframework.jdbc.core.JdbcTemplate;
import pl.cezarysanecki.exampledomain.events.EventB;

import java.util.HashMap;
import java.util.Map;

public record StringDashboard(Map<Character, Integer> numberOfLetters) {
}

class StringDashboardProjection extends AbstractProjection {

    private final JdbcTemplate jdbcTemplate;

    public StringDashboardProjection(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        projects(EventB.class, this::apply);
    }

    private void apply(EventB event) {
        Map<Character, Integer> numberOfLetters = countLetters(event.value());

        numberOfLetters.forEach((key, value) -> jdbcTemplate.update(
                "INSERT into string_projection (letter, amount) " +
                        "VALUES (?, ?) " +
                        "ON CONFLICT (letter) DO UPDATE " +
                        "SET amount = EXCLUDED.amount + ?",
                key,
                value,
                value
        ));
    }

    private Map<Character, Integer> countLetters(String word) {
        Map<Character, Integer> numberOfLetters = new HashMap<>();
        for (char letter : word.toCharArray()) {
            Integer number = numberOfLetters.getOrDefault(letter, 0);
            numberOfLetters.put(letter, number + 1);
        }
        return numberOfLetters;
    }

}


