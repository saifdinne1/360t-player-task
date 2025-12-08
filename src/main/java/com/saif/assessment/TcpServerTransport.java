package com.saif.assessment;
import java.io.IOException; import java.net.ServerSocket; import java.net.Socket; import java.util.concurrent.Executors;
/** Server-side transport for the responder process. */
public class TcpServerTransport implements Transport {
  private final int port; private volatile Player local; private ServerSocket server; private TcpIO io;
  public TcpServerTransport(int port){ this.port=port; }
  public void register(Player p){ this.local=p; }
  public void send(String from,String to,String payload){ if(io==null) throw new IllegalStateException("No TCP connection yet"); io.sendLine(from+"|"+payload); }
  public void start() throws IOException {
    server = new ServerSocket(port);
    System.out.println("[TcpServer] Listening on " + port);
    Socket s = server.accept();
    System.out.println("[TcpServer] Connected: " + s);
    io = new TcpIO(s);
    Executors.newSingleThreadExecutor().submit(() -> {
      try {
        String line;
        while ((line = io.readLine()) != null) {
          if ("TERMINATE".equals(line)) { System.out.println("[TcpServer] TERMINATE. Closing."); break; }
          String[] parts = line.split("\\|", 2);
          String from = parts.length>0?parts[0]:"?"; String payload = parts.length>1?parts[1]:"";
          if (local != null) local.onMessage(from, payload);
        }
      } catch (IOException e) {
        System.out.println("[TcpServer] Reader closed: " + e.getMessage());
      } finally { close(); }
    });
  }
  public void close(){ try{ if(io!=null) io.close(); }catch(Exception ignored){} try{ if(server!=null) server.close(); }catch(Exception ignored){} }
}
