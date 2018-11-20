import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class CentralClient {

    private Socket controlSocket;
//    private DataOutputStream outToServer;
//    private DataInputStream inFromServer;

    String serverAddr;
    int controlPort;
    Path rootDirPath;

    final static String CRLF = "\r\n";

    public Socket getControlSocket() {
        return controlSocket;
    }

    public CentralClient(String serverAddr, int controlPort, Path rootDirPath) {
        this.serverAddr = serverAddr;
        this.controlPort = controlPort;
        this.rootDirPath = rootDirPath;
    }

    public void connect() {
        connect(this.serverAddr, this.controlPort);
    }

    public void connect(String serverAddr, int port) {
//        try (Socket s = new Socket(serverAddr, port)) {
        try {
            this.controlSocket = new Socket(serverAddr, port);
//            controlSocket = s;
            System.out.println("You are connected to " + serverAddr + ":" + port);

            // This should get wrapped in try-with-resources, but...issues.
//            outToServer = new DataOutputStream(controlSocket.getOutputStream());
//            inFromServer = new DataInputStream(new BufferedInputStream(controlSocket.getInputStream()));

        } catch (IOException e) {
            System.err.print(e);
        }
    }

    /**
     * Send the searchTerm to the CentralServer thread, return the results
     * @param searchTerm
     * @return results ArrayList<FileInfo> to display in Client UI
     */
    public ArrayList<FileInfo> search(String searchTerm){
        try{
            DataOutputStream outToServer = new DataOutputStream(controlSocket.getOutputStream());
            outToServer.writeBytes(searchTerm + CRLF);
        } catch (IOException e) {
            System.err.println(e);
        }

        return FileInfo.recvFileInfoArrayList(controlSocket);
    }

    /**
     * List all files on the server
     * @param out Output socket
     * @param dir Directory to list files from
     * @param hostInfo Host Info for creating FileInfo
     */
    public void list(Info hostInfo) {
        ArrayList<FileInfo> files = new ArrayList<>();

        try {
//                stream.forEach(file -> {files.add(file)});
            DirectoryStream<Path> stream = Files.newDirectoryStream(this.rootDirPath);
            for (Path file : stream) {
                files.add(new FileInfo(hostInfo, file.getFileName().toString()));
//                    dataOutToClient.writeUTF(file.getFileName().toString() + CRLF);
                // System.out.println(file.getFileName().toString());
            }

            // Write files list to output socket.
            try {
                FileInfo.sendFileInfoArrayList(this.controlSocket, files);
            } catch (Exception e) {
                System.out.println(e);
            }

        } catch (IOException | DirectoryIteratorException x) {
            // IOException can never be thrown by the iteration.
            // In this snippet, it can only be thrown by newDirectoryStream.
            System.err.println(x);
        }
//            out.close();
        // System.out.println("Data Socket closed");
    }


}
