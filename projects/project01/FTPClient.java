import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;

class FTPClient {

    public static String getUTF8String(InputStream in) throws IOException {
        Reader r = new InputStreamReader(in, "UTF-8");
        r = new BufferedReader(r, 1024);
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = r.read()) != -1) {
            sb.append((char) c);
        }
        return sb.toString();
    }

    public static void main(String argv[]) throws Exception {
        String sentence;
        String modifiedSentence;
        boolean isOpen = true;
        int number = 1;

        Socket controlSocket;
        DataOutputStream outToServer;
        DataInputStream inFromServer;
        int controlPort, dataPort;

        boolean notEnd = true;
        String statusCode;
        boolean clientgo = true;

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        sentence = inFromUser.readLine();
        StringTokenizer tokens = new StringTokenizer(sentence);

        if (sentence.startsWith("CONNECT")) {
            String serverName = tokens.nextToken(); // pass the connect command
            serverName = tokens.nextToken();
            controlPort = Integer.parseInt(tokens.nextToken());
            dataPort = controlPort + 1;

            System.out.print("You are connecting to " + serverName + "\n");
            System.out.print("On dataPort:  " + controlPort + "\n");

            controlSocket = new Socket(serverName, controlPort);
            // try {
            //     controlSocket = new Socket(serverName, controlPort);
            // } catch (Exception e) {
            //     System.out.println(e);
            // }

            System.out.println("You are connected to " + serverName + ":" + controlPort);

            while (isOpen && clientgo) {

                outToServer = new DataOutputStream(controlSocket.getOutputStream());

                inFromServer = new DataInputStream(new BufferedInputStream(controlSocket.getInputStream()));

                sentence = inFromUser.readLine();

                if (sentence.equals("LIST")) {
                    dataPort = dataPort + 2;
                    System.out.print("writing command to server: " + dataPort + " " + sentence + " " + '\n');

                    outToServer.writeBytes(dataPort + " " + sentence + " " + '\n');

                    ServerSocket server = new ServerSocket(dataPort);
                    Socket dataSocket = server.accept();

                    try (DataInputStream inData = new DataInputStream(
                                new BufferedInputStream(dataSocket.getInputStream()))) {

                        modifiedSentence = getUTF8String(inData);
                        System.out.println(modifiedSentence);

                    } catch (IOException e) {
                        // Output unexpected IOExceptions.
                        System.out.println(e);
                    }

                    dataSocket.close();
                    server.close();
                    System.out.println("datasocket is closed");
                    System.out.println("\nWhat would you like to do next:\n retr: file.txt ||stor: file.txt  || close");

                } else if (sentence.startsWith("RETR ")) {
                    dataPort = dataPort + 2;

                    System.out.print("writing command to server:\n" + dataPort + " " + sentence + " " + '\n');
                    outToServer.writeBytes(dataPort + " " + sentence + " " + '\n');

                    ServerSocket server = new ServerSocket(dataPort);
                    Socket dataSocket = server.accept();
                    DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));

                    // ......................
                    dataSocket.close();
                } else if (sentence.startsWith("STOR ")) {
                    dataPort = dataPort + 2;
                    outToServer.writeBytes(dataPort + " " + sentence + " " + '\n');

                    ServerSocket server = new ServerSocket(dataPort);
                    Socket dataSocket = server.accept();
                    DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));

                    // ......................
                    dataSocket.close();
                } else if (sentence.equals("QUIT")) {
                    isOpen = false;
                    clientgo = false;

                    System.out.println("Client is disconnecting");
                    controlSocket.close();
                } else {
                    System.out.println("\nCommand not recognized\n");
                }
            }
        } else {
            System.out.println("\nCommand not recognized:\nPlease CONNECT to a server first.");
        }
    }
}
