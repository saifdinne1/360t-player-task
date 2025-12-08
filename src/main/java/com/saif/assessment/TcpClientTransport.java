package com.saif.assessment;
import java.io.IOException; import java.net.Socket; import java.util.concurrent.Executors;
/** Client-side transport for the initiator process. */
public class TcpClientTransport implements Transport {
  private final String host; private final int port; private volatile Player local; private TcpIO io;
  public TcpClientTransport(String host,int port){ this.host=host; this.port=port; }
  public void register(Player p){ this.local=p; }
  public void send(String from,String to,String payload){ if(io==null) throw new IllegalStateException("Not connected yet"); io.sendLine(from+"|"+payload); }
  public void start() throws IOException {
    Socket s = new Socket(host, port);
    System.out.println("[TcpClient] Connected to " + host + ":" + port);
    io = new TcpIO(s);
    Executors.newSingleThreadExecutor().submit(() -> {
      try {
        String line;
        while ((line = io.readLine()) != null) {
          if ("TERMINATE".equals(line)) { System.out.println("[TcpClient] TERMINATE. Closing."); break; }
          String[] parts = line.split("\\|", 2);
          String from = parts.length>0?parts[0]:"?"; String payload = parts.length>1?parts[1]:"";
          if (local != null) local.onMessage(from, payload);
        }
      } catch (IOException e) {
        System.out.println("[TcpClient] Reader closed: " + e.getMessage());
      } finally { close(); }
    });
  }
  public void sendTerminate(){ if (io!=null) io.sendLine("TERMINATE"); }
  public void close(){ try{ if(io!=null) io.close(); }catch(Exception ignored){} }
}
