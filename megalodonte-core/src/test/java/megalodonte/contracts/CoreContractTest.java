package megalodonte.contracts;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import megalodonte.base.scale.ScaleProvider;
import megalodonte.base.state.State;
import megalodonte.base.state.Subscription;
import megalodonte.base.theme.ThemeTypography;

public final class CoreContractTest {
    static void require(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
    public static void main(String[] args) {
        State<Integer> source = State.of(0);
        List<Integer> seen = new ArrayList<>();
        Subscription binding = source.map(value -> value % 2).observe(seen::add);
        source.set(2); source.set(3);
        require(seen.equals(List.of(0, 1)), "Mapped state must skip equal mapped values");
        binding.close(); binding.close(); source.set(4);
        require(seen.equals(List.of(0, 1)), "Disposable mapping must stop notifications");
        require(source.map(Object::toString).get().equals("4"), "Unobserved derived state must read current value");
        ScaleProvider.setDetector(() -> 2.0);
        require(new ThemeTypography(18,16,14,12).body() == 28, "Shared tokens must use platform scale");
        ScaleProvider.setScale(1.0);
        require(new ThemeTypography(18,16,14,12).body() == 14, "Android logical units must avoid density double scaling");
        ScaleProvider.setDetector(() -> Double.NaN);
        require(ScaleProvider.factor() == 1.0, "Invalid platform scale must have deterministic fallback");
        BackendContract backend = new BackendContract("test", 1, Set.of(Capability.TEXT));
        backend.require(Capability.TEXT);
        try { backend.require(Capability.DESKTOP_WINDOWS); throw new AssertionError("Unsupported capability accepted"); }
        catch (UnsupportedOperationException expected) { }
        try { new BackendContract("test", 2, Set.of()); throw new AssertionError("Incompatible version accepted"); }
        catch (IllegalArgumentException expected) { }
        RouteMatcher<String> routes = new RouteMatcher<>(List.of("users/${id}", "users/new"), value -> value);
        require(routes.resolve("users/new").orElseThrow().route().equals("users/new"), "Literal route must precede parameter route");
        require(routes.resolve("users/42").orElseThrow().params().equals(java.util.Map.of("id", "42")), "Route parameters must be shared");
        require(routes.resolve("missing").isEmpty(), "Unknown route must not match");
        try {
            new RouteMatcher<>(List.of("${a}/detail", "users/${b}"), value -> value).resolve("users/detail");
            throw new AssertionError("Ambiguous route accepted");
        } catch (IllegalArgumentException expected) { }
        System.out.println("Shared state, theme and capability contract tests passed");
    }
}
