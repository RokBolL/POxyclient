package ru.shun.arasakafabric.event;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
public class EventBus {
    private static final HashMap<Class<?>, List<Consumer<?>>> listeners = new HashMap<>();
    public static <T extends Event> void subscribe(Class<T> eventClass, Consumer<T> consumer) {
        listeners.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(consumer);
    }
    @SuppressWarnings("unchecked")
    public static <T extends Event> void post(T event) {
        List<Consumer<?>> consumers = listeners.get(event.getClass());
        if (consumers != null) {
            for (Consumer<?> consumer : consumers) {
                ((Consumer<T>) consumer).accept(event);
            }
        }
    }
}
