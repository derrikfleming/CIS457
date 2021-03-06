import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.net.*;
import java.util.*;

// import FTPHelper;

final class FTPRequest implements Runnable {
    final static String CRLF = "\r\n";
    Socket controlSocket;
    BufferedReader inFromClient;
    DataOutputStream outToClient;
    // The root directory for the ftp server.
    Path ftpRootDir = Paths.get("./ftp_server_root_dir");

    /**
     * FTPRequest Constructor
     * @param  socket    TCP socket to send/recieve control commands over.
     * @return           New FTPRequest Object
     * @throws Exception If an error occurs.
     */
    public FTPRequest(Socket socket) throws Exception {
        try {
            controlSocket = socket;
            outToClient = new DataOutputStream(controlSocket.getOutputStream());
            inFromClient = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * List all files on the server
     * @param port Port number to connect to client on.
     */
    void list(int port) {
        try {
            Socket dataSocket = new Socket(controlSocket.getInetAddress(), port);
            DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(ftpRootDir)) {
                for (Path file : stream) {
                    dataOutToClient.writeUTF(file.getFileName().toString() + CRLF);
                    // System.out.println(file.getFileName().toString());
                }
            } catch (IOException | DirectoryIteratorException x) {
                // IOException can never be thrown by the iteration.
                // In this snippet, it can only be thrown by newDirectoryStream.
                System.err.println(x);
            }
            dataOutToClient.flush();
            dataSocket.close();
            // System.out.println("Data Socket closed");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Store a file on the server
     * @param port     Port number to connect to client on.
     * @param filename Name of file to store.
     */
    void stor(int port, String filename) {
        try (Socket dataSocket = new Socket(controlSocket.getInetAddress(), port)) {
            try {
                Path file = Paths.get(ftpRootDir + "/" + filename);
                FTPHelper.recvFile(dataSocket, file);
            } catch (Exception e) {
                System.out.println(e);
            }
            dataSocket.close();
            System.out.println("datasocket is closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve a file from the server
     * @param port     Port number to connect to client on.
     * @param filename Name of file to retrieve.
     */
    void retr(int port, String filename) {
        try (Socket dataSocket = new Socket(controlSocket.getInetAddress(), port)) {
            try {
                Path file = Paths.get(ftpRootDir + "/" + filename);
                FTPHelper.sendFile(dataSocket, file);
            } catch (Exception e) {
                System.out.println(e);
            }
            dataSocket.close();
            System.out.println("Data Socket closed");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Implement the run() method of the Runnable interface.
     */
    public void run() {
        try {
            System.out.println("A server thread has started. \nGetting ready to process req's");
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Process client requests.
     * @throws Exception If an error occurs.
     */
    private void processRequest() throws Exception {
        String clientsCommand = "";
        String clientCommand = "";
        String commandTarget = "";
        String frstln;
        byte[] data;
        int port;

        while (this.controlSocket.isConnected()) {
            System.out.println("Waiting for requests...");
            clientsCommand = inFromClient.readLine();

            if (clientCommand == null) {
                break;
            } else {
                System.out.println("Command from client: " + clientsCommand);

                StringTokenizer tokens = new StringTokenizer(clientsCommand);
                frstln = tokens.nextToken();
                port = Integer.parseInt(frstln);
                clientCommand = tokens.nextToken();
                if (tokens.hasMoreTokens()) {
                    commandTarget = FTPHelper.getTokensToString(tokens);
                }

                if (clientCommand.equals("LIST")) {
                    list(port);
                } else if (clientCommand.equals("RETR")) {
                    retr(port, commandTarget);
                } else if (clientCommand.equals("STOR")) {
                    stor(port, commandTarget);
                } else if (clientCommand.equals("QUIT")) {
                    System.out.println("Terminating connection with client");
                    break;
                } else {
                    // Twiddle thumbs...
                }
            }
        }
        this.controlSocket.close();
        return;
    }
}
