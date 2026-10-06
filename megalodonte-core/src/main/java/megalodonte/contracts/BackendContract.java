package megalodonte.contracts;

import java.util.Set;
import java.util.Objects;

/** Capability declarations are promises backed by adapter tests, not inferred from class names. */
public record BackendContract(String platform, int contractVersion, Set<Capability> capabilities) {
    public static final int CURRENT_VERSION = 1;
    public BackendContract {
        Objects.requireNonNull(platform);
        if (contractVersion != CURRENT_VERSION) throw new IllegalArgumentException("Unsupported contract version: " + contractVersion);
        capabilities = Set.copyOf(capabilities);
    }
    public boolean supports(Capability capability) { return capabilities.contains(capability); }
    public void require(Capability capability) {
        if (!supports(capability)) throw new UnsupportedOperationException(platform + " does not implement " + capability + " in contract v" + contractVersion);
    }
}
