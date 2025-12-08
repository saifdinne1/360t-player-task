package com.saif.assessment;
/** Responder process: listens, auto-replies, stops on TERMINATE. */
public class AppTcpResponder {
  public static void main(String[] args) throws Exception {
    int port = args.length>0? Integer.parseInt(args[0]) : 5001;
    TcpServerTransport t = new TcpServerTransport(port);
    BasicPlayer responder = new BasicPlayer("B", t, true);
    t.register(responder);
    t.start();
    while (true) Thread.sleep(200); // keep alive; reader thread closes on TERMINATE
  }
}
