package megalodonte.base.state;

/**
 * Read-only view of an observable state container. Provides read access
 * and subscription without write capability.
 *
 * @param <T> the type of the held value
 */
public interface ReadableState<T> {
    /** Returns the current value. */
    T get();

    /** Returns true if the current value is null. */
    boolean isNull();

    /** Subscribes a listener to value changes. Called immediately with the current value. */
    void subscribe(java.util.function.Consumer<T> listener);

    default boolean unsubscribe(java.util.function.Consumer<T> listener) {
        throw new UnsupportedOperationException("This state implementation does not support disposable subscriptions");
    }

    default Subscription observe(java.util.function.Consumer<T> listener) {
        subscribe(listener);
        return Subscription.once(() -> unsubscribe(listener));
    }

    /**
     * Creates a derived state by applying a mapping function. The derived state
     * updates automatically whenever this state changes.
     *
     * @param mapper the transformation to apply
     * @param <R>    the type of the derived state
     * @return a new read-only state that tracks this state through the mapper
     */
    default <R> ReadableState<R> map(java.util.function.Function<T, R> mapper) {
        return new MappedState<>(this, mapper);
    }
}
