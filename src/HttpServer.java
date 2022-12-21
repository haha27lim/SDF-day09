import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Executor;

class HttpServer {
    private int port;
    private List<String> docRoots;
    private Executor executor;
  
    public HttpServer(int port, List<String> docRoots, Executor executor) {
      this.port = port;
      this.docRoots = docRoots;
      this.executor = executor;
    }
  
    public void start() {
      try (ServerSocket serverSocket = new ServerSocket(port)) {
        while (true) {
          Socket clientSocket = serverSocket.accept();
          executor.execute(new HttpClientConnection(clientSocket, docRoots));
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }