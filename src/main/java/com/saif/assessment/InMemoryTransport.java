package com.saif.assessment;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
/** Same-process delivery via direct method call. */
public class InMemoryTransport implements Transport {
    private final Map<String, Player> registry = new ConcurrentHashMap<>();
    public void register(Player player) {
        registry.put(Objects.requireNonNull(player.id()), Objects.requireNonNull(player));
    }
    public void send(String from, String to, String payload) {
        Player target = registry.get(Objects.requireNonNull(to));
        if (target == null) throw new IllegalArgumentException("Unknown player: " + to);
        target.onMessage(from, payload);
    }
    public void close() {}
}
