package megalodonte.base.state;

import java.util.concurrent.atomic.AtomicBoolean;

@FunctionalInterface
public interface Subscription extends AutoCloseable {
    @Override void close();
    static Subscription once(Runnable cleanup) {
        AtomicBoolean closed = new AtomicBoolean();
        return () -> { if (closed.compareAndSet(false, true)) cleanup.run(); };
    }
}
