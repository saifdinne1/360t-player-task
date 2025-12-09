// src/main/java/com/saif/assessment/AppTcpResponder.java
package com.saif.assessment;

/** Responder process (Player B): auto-replies and exits cleanly after TERMINATE. */
public class AppTcpResponder {
    public static void main(String[] args) throws Exception {
        int port = (args.length > 0) ? Integer.parseInt(args[0]) : 5001;
        TcpServerTransport transport = new TcpServerTransport(port);
        BasicPlayer responder = new BasicPlayer("B", transport, true); // auto-reply
        transport.register(responder);
        transport.start();
        transport.awaitClose();                 // <— blocks until TERMINATE handled
        System.out.println("[Responder] Bye.");
    }
}
