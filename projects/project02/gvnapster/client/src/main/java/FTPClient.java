import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FTPClient implements Runnable {
    private FileInfo fileInfo;
    private Path ftpRootDir;
    private DataOutputStream outToServer;

    final static String CRLF = "\r\n";

    public FTPClient(FileInfo fileInfo, Path ftpRootDir) {
        this.fileInfo = fileInfo;
        this.ftpRootDir = ftpRootDir;
    }

    /**
     * Implement the run() method of the Runnable interface.
     */
    public void run() {
        try {
            System.out.println("FTPClient thread has started.");
            this.retr();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void retr() {
        // Retrieve a file from the server
//        dataPort = dataPort + 2;

        try (ServerSocket server = new ServerSocket(fileInfo.getPort())) {
            // Build the retr command to send to server.
            String command = fileInfo.getPort() + " RETR " + fileInfo.getFilename() + " " + CRLF;
            // System.out.println("writing command to server:" + command);
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
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
