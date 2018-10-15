import java.io.*;
import java.net.*;
import java.util.*;

final class FTPRequest implements Runnable {
    final static String CRLF = "\r\n";
    Socket controlSocket;
    BufferedReader inFromClient;
    DataOutputStream outToClient;

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

    void list(int port) {
        try {
            Socket dataSocket = new Socket(controlSocket.getInetAddress(), port);
            DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

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

            dataSocket.close();
            System.out.println("Data Socket closed");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Implement the run() method of the Runnable interface.
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception {
        String fromClient;
        String clientCommand;
        byte[] data;
        int port;

        ServerSocket welcomeSocket = new ServerSocket(12000);
        String frstln;

        while (true) {
            fromClient = inFromClient.readLine();

            System.out.println("Command from client: " + inFromClient);

            StringTokenizer tokens = new StringTokenizer(fromClient);
            frstln = tokens.nextToken();
            port = Integer.parseInt(frstln);
            clientCommand = tokens.nextToken();

            if (clientCommand.equals("list:")) {
                list(port);
            } else if (clientCommand.equals("retr:")) {
                retr(port);
            } else if (clientCommand.equals("stor:")) {
                stor(port);
            } else if (clientCommand.equals("quit:")) {

            } else {

            }
        }

        // ......................

    }
}
