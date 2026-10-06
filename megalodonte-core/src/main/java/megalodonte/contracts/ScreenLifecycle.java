package megalodonte.contracts;

public interface ScreenLifecycle {
    default void onMount() { }
    default void onDestroy() { }
}
