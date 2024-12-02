package pl.cezarysanecki.projections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface Projection {

    List<Class> handles();

    void handle(Object event);

}

abstract class AbstractProjection implements Projection {

    private Map<Class, Consumer<Object>> handlers = new HashMap<>();

    @Override
    public List<Class> handles() {
        return handlers.keySet().stream().toList();
    }

    public <Event> void projects(Class<Event> type, Consumer<Event> action) {
        handlers.put(
                type,
                event -> action.accept((Event) event)
        );
    }

    @Override
    public void handle(Object event) {
        handlers.get(event.getClass()).accept(event);
    }
}
