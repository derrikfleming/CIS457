import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.net.*;
import java.util.*;

final class FTPRequest implements Runnable {
    final static String CRLF = "\r\n";
    Socket controlSocket;
    BufferedReader inFromClient;
    DataOutputStream outToClient;
    // The root directory for the ftp server.
    Path ftpRootDir = Paths.get("./ftp_server_root_dir");

    // Constructor
    public FTPRequest(Socket socket) throws Exception {
        try {
            controlSocket = socket;
            outToClient = new DataOutputStream(controlSocket.getOutputStream());
            inFromClient = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // List all files on the server
    void list(int port) {
        try {
            Socket dataSocket = new Socket(controlSocket.getInetAddress(), port);
            DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
            // ......................
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

    // Store a file on the server
    void stor(int port, String filename) {

        try {
            Socket dataSocket = new Socket(controlSocket.getInetAddress(), port);
            DataInputStream dataOutToClient = new DataInputStream(dataSocket.getInputStream());
            Reader reader = new InputStreamReader(dataSocket.getInputStream());
            BufferedReader fin = new BufferedReader(reader);
            Writer writer = new OutputStreamWriter(
                    new FileOutputStream(new File(ftpRootDir.toString() + "/" + filename)), "UTF-8");
            BufferedWriter fout = new BufferedWriter(writer);
            String s;
            boolean firstline = true;
            while ((s = fin.readLine()) != null) {
                // System.out.println(s);
                if (firstline == false)
                    fout.newLine();
                else
                    firstline = false;
                fout.write(s);

            }

            fin.close();
            fout.close();
            dataSocket.close();
            System.out.println("datasocket is closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Retrieve a file from the server
    void retr(int port, String filename) {
        try {
            Socket dataSocket = new Socket(controlSocket.getInetAddress(), port);
            DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
            // ......................

            Path file = Paths.get(ftpRootDir + "/" + filename);

            try {
                Reader reader = new InputStreamReader(new FileInputStream(file.toString()));
                BufferedReader fin = new BufferedReader(reader);
                Writer writer = new OutputStreamWriter(dataOutToClient, "UTF-8");
                BufferedWriter fout = new BufferedWriter(writer);
                String s;
                boolean firstline = true;
                while ((s = fin.readLine()) != null) {
                    // System.out.println(s);
                    if (firstline == false)
                        fout.newLine();
                    else
                        firstline = false;
                    fout.write(s);
                }
                fin.close();
                fout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            dataSocket.close();
            System.out.println("Data Socket closed");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Implement the run() method of the Runnable interface.
    public void run() {
        try {
            System.out.println("A server thread has started. \nGetting ready to process req's");
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception {
        String clientsCommand = "";
        String clientCommand = "";
        String commandTarget = "";
        byte[] data;
        int port;

        String frstln;

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
                    commandTarget = FTPClient.getTokensToString(tokens);
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

                }
            }

        }
        this.controlSocket.close();
        return;
    }
}
