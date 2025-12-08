package com.saif.assessment;
import java.util.concurrent.CountDownLatch;
/** Initiator: connects, sends 10, waits 10 replies, sends TERMINATE, exits. */
public class AppTcpInitiator {
  public static void main(String[] args) throws Exception {
    String host = args.length>0? args[0] : "localhost";
    int port  = args.length>1? Integer.parseInt(args[1]) : 5001;
    TcpClientTransport t = new TcpClientTransport(host, port);
    CountDownLatch latch = new CountDownLatch(10);
    BasicPlayer a = new BasicPlayer("A", t, false, latch);
    t.register(a);
    t.start();
    for (int i=1;i<=10;i++){ a.send("B", "hello-" + i); Thread.sleep(20); }
    latch.await();
    System.out.println("[AppTcpInitiator] 10 replies received. TERMINATE.");
    t.sendTerminate();
    Thread.sleep(100);
    t.close();
  }
}
