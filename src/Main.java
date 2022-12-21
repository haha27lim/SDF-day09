import java.io.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Main {
  public static void main(String[] args) {
    // Parse command line options
    int port = 8080;
    List<String> docRoots = new ArrayList<>();
    docRoots.add("./static");
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("--port")) {
        port = Integer.parseInt(args[++i]);
      } else if (args[i].equals("--docRoot")) {
        String[] paths = args[++i].split(":");
        docRoots.addAll(Arrays.asList(paths));
      }
    }

    // Check docRoot paths and exit if any of them are invalid
    for (String path : docRoots) {
      File docRoot = new File(path);
      if (!docRoot.exists() || !docRoot.isDirectory() || !docRoot.canRead()) {
        System.out.println(path + " is not a valid docRoot path");
        System.exit(1);
      }
    }

    // Create thread pool with 3 threads
    Executor executor = Executors.newFixedThreadPool(3);

    // Start HTTP server
    HttpServer server = new HttpServer(port, docRoots, executor);
    server.start();
  }
}