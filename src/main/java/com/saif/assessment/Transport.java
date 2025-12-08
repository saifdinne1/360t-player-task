package com.saif.assessment;
/** Abstracts message delivery; can be in-memory or TCP. */
public interface Transport extends AutoCloseable {
    void register(Player player);
    void send(String from, String to, String payload);
    void close();
}
