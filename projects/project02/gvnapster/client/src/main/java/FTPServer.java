import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;

public class FTPServer implements Runnable {
    // Control Port
    private int port;
    // The root directory for the ftp server.
    private Path ftpRootDir;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Path getFtpRootDir() {
        return ftpRootDir;
    }

    public void setFtpRootDir(Path ftpRootDir) {
        this.ftpRootDir = ftpRootDir;
    }

    // TODO: Modify GUI to allow setting control port at runtime.
    // TODO: Modify GUI to allow setting ftp root directory at runtime.
    public FTPServer(int port, Path ftpRootDir) {
        this.port = port;
        this.ftpRootDir = ftpRootDir;
    }

    public FTPServer() {
    }

    /**
     * Implement the run() method of the Runnable interface.
     */
    public void run() {
        System.out.println("Main server thread has started.");
        System.out.println("Getting ready to process req's");

        try (
                // Establish the listen socket.
                ServerSocket controlSocket = new ServerSocket(port);
        ) {
            // Process HTTP service requests in an infinite loop.
            while (!controlSocket.isClosed()) {
                // Listen for a TCP connection request.
                Socket connection = controlSocket.accept();
                // Construct an object to process the HTTP request message.
                FTPRequest request = new FTPRequest(connection, ftpRootDir);
                // Create a new thread to process the request.
                Thread thread = new Thread(request);
                // Start the thread.
                thread.start();
            }
            controlSocket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
