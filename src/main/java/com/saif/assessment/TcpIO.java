package com.saif.assessment;
import java.io.*; import java.net.Socket; import java.util.Objects; import java.util.concurrent.atomic.AtomicBoolean;
/** Line-based TCP I/O helper. */
class TcpIO implements Closeable {
  protected final Socket socket; protected final BufferedReader in; protected final PrintWriter out;
  protected final AtomicBoolean closed = new AtomicBoolean(false);
  TcpIO(Socket socket) throws IOException {
    this.socket = Objects.requireNonNull(socket);
    this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
  }
  public void sendLine(String line) { out.println(line); }
  public String readLine() throws IOException { return in.readLine(); }
  public void close() {
    if (closed.compareAndSet(false,true)) {
      try{in.close();}catch(Exception ignored){} try{out.close();}catch(Exception ignored){} try{socket.close();}catch(Exception ignored){}
    }
  }
}
