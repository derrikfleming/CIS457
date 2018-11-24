import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FTPClient implements Runnable {
    private FileInfo fileInfo;
    private Path ftpRootDir;

    private Socket controlSocket;
    private DataOutputStream outToServer;
    private DataInputStream inFromServer;
    int controlPort, dataPort;

    final static String CRLF = "\r\n";

    public FTPClient(FileInfo fileInfo, Path ftpRootDir) {
        this.fileInfo = fileInfo;
        this.ftpRootDir = ftpRootDir;

//        controlSocket = new Socket();
        controlPort = fileInfo.getPort();
        dataPort = controlPort + 1;
    }

    /**
     * Implement the run() method of the Runnable interface.
     */
    public void run() {
        try {
            System.out.println("FTPClient thread has started.");

            this.connect();
            this.retr();


            controlSocket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void connect() {
        try {
            controlSocket = new Socket(fileInfo.getAddress(), fileInfo.getPort());
            System.out.println("You are connected to " + fileInfo.getAddress() + ":" + controlPort);

            // This should get wrapped in try-with-resources, but...issues.
            outToServer = new DataOutputStream(controlSocket.getOutputStream());
            inFromServer = new DataInputStream(new BufferedInputStream(controlSocket.getInputStream()));

        } catch (IOException e) {
            System.err.print(e);
        }

    }

    private void retr() {
        // Retrieve a file from the server
        dataPort = dataPort + 2;

        try (ServerSocket server = new ServerSocket(dataPort)) {
            // Build the retr command to send to server.
            String command = dataPort + " RETR " + fileInfo.getFilename() + " " + CRLF;
             System.out.println("writing command to server:" + command);
            outToServer.writeBytes(command);

            try (Socket dataSocket = server.accept()) {
                Path file = Paths.get(ftpRootDir.toString(), fileInfo.getFilename());
                // Retrieve a file from the server
                FTPHelper.recvFile(dataSocket, file);
                dataSocket.close();
                System.out.println("datasocket is closed");
            } catch (Exception e) {
                System.out.println(e);
            }
            server.close();
            System.out.println("serversocket is closed");
            controlSocket.close();
            System.out.println("controlsocket is closed");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
