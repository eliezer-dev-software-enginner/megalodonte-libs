package megalodonte.base.theme;

public final class FontLoader {
    public static final String DEFAULT_FONTS_DIRECTORY = "assets/fonts";
    private FontLoader() { }
    public static int loadAll() { return loadAll(DEFAULT_FONTS_DIRECTORY); }
    public static int loadAll(String resourceDirectory) { return ThemePlatform.loadFonts(resourceDirectory); }
}
