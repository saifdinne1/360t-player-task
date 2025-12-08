package com.saif.assessment;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
/** Basic player with a send-counter; can auto-reply or not. */
public class BasicPlayer implements Player {
    private final String id;
    private final Transport transport;
    private final boolean autoReply;
    private final AtomicInteger sentCounter = new AtomicInteger(0);
    private final CountDownLatch replyLatch; // only for initiator

    public BasicPlayer(String id, Transport transport, boolean autoReply) {
        this(id, transport, autoReply, null);
    }
    public BasicPlayer(String id, Transport transport, boolean autoReply, CountDownLatch replyLatch) {
        this.id = Objects.requireNonNull(id);
        this.transport = Objects.requireNonNull(transport);
        this.autoReply = autoReply;
        this.replyLatch = replyLatch;
    }

    public String id() { return id; }

    public void onMessage(String from, String payload) {
        System.out.printf("[%s] received from %s: %s%n", id, from, payload);
        if (autoReply) {
            int next = sentCounter.incrementAndGet();        // increment BEFORE sending
            String reply = payload + "#" + next;             // concat payload + counter
            System.out.printf("[%s] replying to %s: %s%n", id, from, reply);
            transport.send(id, from, reply);
        } else if (replyLatch != null) {
            replyLatch.countDown();                          // initiator just counts replies
        }
    }

    public void send(String to, String payload) {
        int next = sentCounter.incrementAndGet();
        System.out.printf("[%s] sending to %s: %s (count=%d)%n", id, to, payload, next);
        transport.send(id, to, payload);
    }
}
