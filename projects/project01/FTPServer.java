import java.io.*;
import java.net.*;
import java.util.*;

public class FTPServer {
    /**
     * FTPServer main()
     * @param  argv[]    CLI arguments.
     * @throws Exception If an error occurs.
     */
    public static void main(String argv[]) throws Exception {
        // control port
        int port = 9381;
        // Establish the listen socket.
        ServerSocket controlSocket = new ServerSocket(port);
        // Process HTTP service requests in an infinite loop.
        while (!controlSocket.isClosed()) {
            // Listen for a TCP connection request.
            Socket connection = controlSocket.accept();
            // Construct an object to process the HTTP request message.
            FTPRequest request = new FTPRequest(connection);
            // Create a new thread to process the request.
            Thread thread = new Thread(request);
            // Start the thread.
            thread.start();
        }
        controlSocket.close();
    }
}
