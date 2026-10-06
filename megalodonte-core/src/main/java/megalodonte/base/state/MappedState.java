package megalodonte.base.state;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/** Lazy mapping: a derived value only observes its source while it has subscribers. */
final class MappedState<T, R> implements ReadableState<R> {
    private final ReadableState<T> source;
    private final Function<T, R> mapper;
    private final Map<Consumer<R>, List<Subscription>> registrations = new IdentityHashMap<>();
    MappedState(ReadableState<T> source, Function<T, R> mapper) {
        this.source = source; this.mapper = Objects.requireNonNull(mapper);
    }
    @Override public R get() { return mapper.apply(source.get()); }
    @Override public boolean isNull() { return get() == null; }
    @Override public void subscribe(Consumer<R> listener) { observe(listener); }
    @Override public Subscription observe(Consumer<R> listener) {
        Objects.requireNonNull(listener);
        Consumer<T> mapped = new Consumer<>() {
            boolean initialized;
            R previous;
            @Override public void accept(T value) {
                R next = mapper.apply(value);
                if (!initialized || !Objects.equals(previous, next)) {
                    initialized = true; previous = next; listener.accept(next);
                }
            }
        };
        Subscription parent = source.observe(mapped);
        List<Subscription> handles = registrations.computeIfAbsent(listener, ignored -> new ArrayList<>());
        Subscription[] holder = new Subscription[1];
        holder[0] = Subscription.once(() -> {
            parent.close(); handles.remove(holder[0]);
            if (handles.isEmpty()) registrations.remove(listener);
        });
        handles.add(holder[0]);
        return holder[0];
    }
    @Override public boolean unsubscribe(Consumer<R> listener) {
        List<Subscription> handles = registrations.get(listener);
        if (handles == null || handles.isEmpty()) return false;
        handles.get(0).close();
        return true;
    }
}
