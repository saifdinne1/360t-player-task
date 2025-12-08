package com.saif.assessment;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertTrue;
/** Validates 10 round-trips in in-memory mode. */
public class InMemoryPingPongTest {
    @Test
    public void tenRoundTripsComplete() throws Exception {
        try (Transport t = new InMemoryTransport()) {
            CountDownLatch latch = new CountDownLatch(10);
            BasicPlayer a = new BasicPlayer("A", t, false, latch);
            BasicPlayer b = new BasicPlayer("B", t, true);
            t.register(a); t.register(b);
            for (int i=1;i<=10;i++) a.send("B", "hello-" + i);
            assertTrue(latch.await(2, TimeUnit.SECONDS));
        }
    }
}
