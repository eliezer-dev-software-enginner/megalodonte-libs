package megalodonte.contracts;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

/** Deterministic route matching shared by platform navigation adapters. */
public final class RouteMatcher<R> {
    public record Match<R>(R route, Map<String, String> params) { }
    private final List<R> routes;
    private final Function<R, String> path;
    public RouteMatcher(Collection<R> routes, Function<R, String> path) {
        this.routes = List.copyOf(routes); this.path = java.util.Objects.requireNonNull(path);
    }
    public Optional<Match<R>> resolve(String requested) {
        String[] values = requested.split("/", -1);
        Match<R> best = null;
        int specificity = -1;
        boolean ambiguous = false;
        for (R route : routes) {
            String[] pattern = path.apply(route).split("/", -1);
            if (pattern.length != values.length) continue;
            Map<String, String> params = new HashMap<>();
            int literals = 0;
            boolean matches = true;
            for (int i = 0; i < pattern.length; i++) {
                String segment = pattern[i];
                if (segment.startsWith("${") && segment.endsWith("}")) {
                    String name = segment.substring(2, segment.length() - 1);
                    if (name.isEmpty() || values[i].isEmpty()) { matches = false; break; }
                    params.put(name, values[i]);
                } else if (segment.equals(values[i])) literals++;
                else { matches = false; break; }
            }
            if (!matches || literals < specificity) continue;
            if (literals == specificity) { ambiguous = true; continue; }
            best = new Match<>(route, Map.copyOf(params)); specificity = literals; ambiguous = false;
        }
        if (ambiguous) throw new IllegalArgumentException("Ambiguous route: " + requested);
        return Optional.ofNullable(best);
    }
}
