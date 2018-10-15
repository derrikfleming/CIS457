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
            outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        } catch (Exception e) {
            System.println(e);
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

        ServerSocket welcomeSocket = new ServerSocket(12000);
        String frstln;

        while (true) {
            fromClient = inFromClient.readLine();

            StringTokenizer tokens = new StringTokenizer(fromClient);
            frstln = tokens.nextToken();
            port = Integer.parseInt(frstln);
            clientCommand = tokens.nextToken();

            if (clientCommand.equals("list:")) {

                Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                DataOutputStream dataOutToClient = new DataOutputStrea(dataSocket.getOutputStream());
                // ..........................

            } else if (clientCommand.equals("retr:")) {
                // ............................................................
            } else if

            dataSocket.close();
            System.out.println("Data Socket closed");
        }

        // ......................


    }
}
