package megalodonte.base.theme;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

/** Transitional host hooks for source-compatible font loading and scene styling. */
public final class ThemePlatform {
    private static ToIntFunction<String> fonts = path -> { throw new UnsupportedOperationException("Custom font discovery requires a platform font loader"); };
    private static Consumer<Object> scenes = scene -> { throw new UnsupportedOperationException("Scene font styling requires a platform adapter"); };
    private ThemePlatform() { }
    public static void install(ToIntFunction<String> fontLoader, Consumer<Object> sceneStyler) {
        fonts = Objects.requireNonNull(fontLoader);
        scenes = Objects.requireNonNull(sceneStyler);
    }
    static int loadFonts(String path) { return fonts.applyAsInt(path); }
    static void styleScene(Object scene) { scenes.accept(scene); }
}
