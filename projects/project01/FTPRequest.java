import java.io.*;
import java.net.*;
import java.util.*;

final class FTPRequest implements Runnable {
    final static String CRLF = "\r\n";
    Socket controlSocket;
    BufferedReader inFromClient;
    DataOutputStream outToClient;

    // Constructor
    public FTPRequest(Socket socket, int threadID) throws Exception {
        try {
            // thread ID unimplemented fully
            System.out.println("Hi, I'm thread: " + threadID);
            controlSocket = socket;
            outToClient = new DataOutputStream(controlSocket.getOutputStream());

            inFromClient = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void list(int port) {
        try {
            Socket dataSocket = new Socket(controlSocket.getInetAddress(), port);
            DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
            // ......................
            dataSocket.close();
            System.out.println("Data Socket closed");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void stor(int port) {
        try {
            Socket dataSocket = new Socket(controlSocket.getInetAddress(), port);
            DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
            // ......................
            dataSocket.close();
            System.out.println("Data Socket closed");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void retr(int port) {
        try {
            Socket dataSocket = new Socket(controlSocket.getInetAddress(), port);
            DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());
            // ......................
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
        String clientsCommand;
        String clientCommand;
        byte[] data;
        int port;

        String frstln;

        while (true) {
            System.out.println("Waiting for requests...");
            clientsCommand = inFromClient.readLine();

            System.out.println("Command from client: " + clientsCommand);

            StringTokenizer tokens = new StringTokenizer(clientsCommand);
            frstln = tokens.nextToken();
            port = Integer.parseInt(frstln);
            clientCommand = tokens.nextToken();

            if (clientCommand.equals("LIST")) {
                list(port);
            } else if (clientCommand.equals("RETR")) {
                retr(port);
            } else if (clientCommand.equals("STOR")) {
                stor(port);
            } else if (clientCommand.equals("QUIT")) {
                System.out.println("Terminating connection with client");
                controlSocket.close();
            } else if (clientCommand.equals("INIT")) {

            } else {

            }
        }
    }
}
