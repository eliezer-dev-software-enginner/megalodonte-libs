package megalodonte.base.theme;

import java.util.Objects;
import megalodonte.base.state.State;

public final class ThemeManager {
    private static final State<ThemeInterface> current = State.of(null);
    private ThemeManager() { }
    public static void setTheme(ThemeInterface theme) { current.set(Objects.requireNonNull(theme)); }
    public static ThemeInterface theme() { return current.get(); }
    public static ThemeInterface getTheme() { return theme(); }
    public static State<ThemeInterface> state() { return current; }
    /** Compatibility entry point. Host code should apply native fonts itself. */
    @Deprecated public static void applyFontFamily(Object scene) {
        if (scene == null || theme() == null || theme().typography().fontFamily() == null
            || theme().typography().fontFamily().isBlank()) return;
        ThemePlatform.styleScene(scene);
    }
}
