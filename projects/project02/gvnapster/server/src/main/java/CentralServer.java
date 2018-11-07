import java.net.ServerSocket;
import java.net.Socket;

public class CentralServer {
    /**
     * FTPServer main()
     * @throws Exception If an error occurs.
     */
    public static void main(String argv[]) throws Exception {

        // for verifying build working
        System.out.println("HEY! The CentralServer is running");

        //testing
        CentralServerRequest request = new CentralServerRequest();


//        // control port
//        int port = 9381;
//        // Establish the listen socket.
//        ServerSocket socket = new ServerSocket(port);
//        // Process HTTP service requests in an infinite loop.
//        while (!socket.isClosed()) {
//            // Listen for a TCP connection request.
//            Socket connection = socket.accept();
//            // Construct an object to process the HTTP request message.
//            CentralServerRequest request = new CentralServerRequest(connection);
//            // Create a new thread to process the request.
//            Thread thread = new Thread(request);
//            // Start the thread.
//            thread.start();
//        }
//        socket.close();
    }
}
