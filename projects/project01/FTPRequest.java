import java.io.*;
import java.net.*;
import java.util.*;

final class FTPRequest implements Runnable {
    final static String CRLF = "\r\n";
    Socket socket;

    // Constructor
    public FTPRequest(Socket socket) throws Exception {
        this.socket = socket;
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
            Socket connectionSocket = welcomeSocket.accept();
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            fromClient = inFromClient.readLine();

            StringTokenizer tokens = new StringTokenizer(fromClient);
            frstln = tokens.nextToken();
            port = Integer.parseInt(frstln);
            clientCommand = tokens.nextToken();

            if (clientCommand.equals("list:")) {

                Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                DataOutputStream dataOutToClient = new DataOutputStrea(dataSocket.getOutputStream());
                // ..........................

            }

            dataSocket.close();
            System.out.println("Data Socket closed");
        }

        // ......................

        if (clientCommand.equals("retr:")) {
            // ............................................................
        }
    }
}
