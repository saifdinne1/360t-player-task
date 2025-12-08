package com.saif.assessment;
/** Player that can send/receive messages. */
public interface Player {
    String id();
    void onMessage(String from, String payload);
    void send(String to, String payload);
}
