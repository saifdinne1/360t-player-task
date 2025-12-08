package com.saif.assessment;
import java.util.concurrent.CountDownLatch;
/** Single-JVM mode: two players, 10 round-trips, exit. */
public class AppLocal {
    public static void main(String[] args) throws Exception {
        try (Transport t = new InMemoryTransport()) {
            CountDownLatch latch = new CountDownLatch(10);
            BasicPlayer a = new BasicPlayer("A", t, false, latch); // initiator
            BasicPlayer b = new BasicPlayer("B", t, true);         // auto-reply
            t.register(a); t.register(b);
            for (int i = 1; i <= 10; i++) a.send("B", "hello-" + i);
            latch.await();
            System.out.println("[AppLocal] Completed 10 round-trips. Bye.");
        }
    }
}
