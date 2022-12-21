import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

class HttpClientConnection implements Runnable {
    private Socket clientSocket;
    private List<String> docRoots;
  
    public HttpClientConnection(Socket clientSocket, List<String> docRoots) {
      this.clientSocket = clientSocket;
      this.docRoots = docRoots;
    }
  
    @Override
    public void run() {
      try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
           BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream())) {
        String requestLine = in.readLine();
        String[] requestElements = requestLine.split(" ");
        String method = requestElements[0];
        String resource = requestElements[1];
  
        if (!method.equals("GET")) {
          // Action 1
          String response = "HTTP/1.1 405 Method Not Allowed\r\n\r\n" + method + " not supported\r\n";
          out.write(response.getBytes());
          out.flush();
          return;
          }
            // Replace / with /index.html
  if (resource.equals("/")) {
    resource = "/index.html";
  }

  // Check if resource exists in any of the docRoot directories
  File resourceFile = null;
  for (String docRoot : docRoots) {
    resourceFile = new File(docRoot + resource);
    if (resourceFile.exists()) {
      break;
    }
  }
  if (resourceFile == null || !resourceFile.exists()) {
    // Action 2
    String response = "HTTP/1.1 404 Not Found\r\n\r\n" + resource + " not found\r\n";
    out.write(response.getBytes());
    out.flush();
    return;
  }

  // Read resource contents into a byte array
  byte[] resourceContents = new byte[(int) resourceFile.length()];
  try (FileInputStream resourceInputStream = new FileInputStream(resourceFile)) {
    resourceInputStream.read(resourceContents);
  }

  if (resource.endsWith(".png")) {
    // Action 4
    String response = "HTTP/1.1 200 OK\r\nContent-Type: image/png\r\n\r\n";
    out.write(response.getBytes());
    out.write(resourceContents);
    out.flush();
    return;
  }

  // Action 3
  String response = "HTTP/1.1 200 OK\r\n\r\n";
  out.write(response.getBytes());
  out.write(resourceContents);
  out.flush();
} catch (IOException e) {
  e.printStackTrace();
} finally {
  try {
    clientSocket.close();
  } catch (IOException e) {
    e.printStackTrace();
  }
}
}
}

