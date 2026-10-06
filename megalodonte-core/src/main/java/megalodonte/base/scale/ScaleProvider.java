package megalodonte.base.scale;

import java.util.Objects;
import java.util.function.DoubleSupplier;

/** Shared scale policy; platform hosts inject detection instead of importing toolkit types. */
public final class ScaleProvider {
    private static Double scaleFactor;
    private static DoubleSupplier detector = () -> 1.0;
    private ScaleProvider() { }
    public static synchronized void setDetector(DoubleSupplier value) { detector = Objects.requireNonNull(value); reset(); }
    public static synchronized void initialize() { factor(); }
    public static synchronized double factor() {
        if (scaleFactor == null) {
            double detected;
            try { detected = detector.getAsDouble(); } catch (RuntimeException error) { detected = 1.0; }
            scaleFactor = Double.isFinite(detected) && detected > 0 ? Math.max(0.5, Math.min(detected, 3.0)) : 1.0;
        }
        return scaleFactor;
    }
    public static int scale(int value) { return (int) Math.round(value * factor()); }
    public static double scale(double value) { return value * factor(); }
    public static synchronized void setScale(double value) {
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Scale must be finite");
        scaleFactor = Math.max(0.25, Math.min(value, 4.0));
    }
    public static synchronized void reset() { scaleFactor = null; }
}
