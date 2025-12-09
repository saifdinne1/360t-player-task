package com.saif.assessment;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server-side transport used by the responder process (Player B).
 * Wire format: "FROM|PAYLOAD\n". Special line "TERMINATE" closes gracefully.
 */
public class TcpServerTransport implements Transport {
    private final int port;
    private volatile Player local;
    private ServerSocket server;
    private volatile TcpIO io;

    private final CountDownLatch closed = new CountDownLatch(1);
    private final ExecutorService reader = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "tcp-server-reader");
        t.setDaemon(true);
        return t;
    });

    public TcpServerTransport(int port) { this.port = port; }

    @Override public void register(Player player) { this.local = Objects.requireNonNull(player); }

    /** Start listening, accept one client, and start the reader loop. */
    public void start() throws IOException {
        server = new ServerSocket(port);
        System.out.println("[TcpServer] Listening on " + port);
        final Socket socket = server.accept(); // single peer for this exercise
        System.out.println("[TcpServer] Connected: " + socket.getRemoteSocketAddress());
        io = new TcpIO(socket);

        reader.submit(() -> {
            try {
                String line;
                while ((line = io.readLine()) != null) {
                    if ("TERMINATE".equals(line)) {
                        System.out.println("[TcpServer] TERMINATE received.");
                        break;
                    }
                    String from; String payload;
                    int idx = line.indexOf('|');
                    if (idx >= 0) { from = line.substring(0, idx); payload = line.substring(idx + 1); }
                    else { from = "?"; payload = line; }
                    Player p = local;
                    if (p != null) p.onMessage(from, payload);
                }
            } catch (IOException e) {
                System.out.println("[TcpServer] Reader closed: " + e.getMessage());
            } finally {
                close();
            }
        });
    }

    @Override public void send(String from, String to, String payload) {
        TcpIO conn = io; if (conn == null) throw new IllegalStateException("No TCP client connected yet");
        conn.sendLine(from + "|" + payload);
    }

    /** Wait until closed (after TERMINATE). */
    public void awaitClose() throws InterruptedException { closed.await(); }

    /** Idempotent cleanup and signal. */
    @Override public void close() {
        try { if (io != null) io.close(); } catch (Exception ignored) {}
        try { if (server != null && !server.isClosed()) server.close(); } catch (Exception ignored) {}
        reader.shutdownNow();
        closed.countDown();
    }
}
