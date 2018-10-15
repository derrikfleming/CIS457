import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;

class FTPClient {

    public static void main(String argv[]) throws Exception {
        String sentence;
        String modifiedSentence;
        boolean isOpen = true;
        int number = 1;
        boolean notEnd = true;
        String statusCode;
        boolean clientgo = true;

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        sentence = inFromUser.readLine();
        StringTokenizer tokens = new StringTokenizer(sentence);

        if (sentence.startsWith("connect")) {
            String serverName = tokens.nextToken(); // pass the connect command
            serverName = tokens.nextToken();
            int controlPort = Integer.parseInt(tokens.nextToken());
            int port = controlPort;
            System.out.println("You are connecting to " + serverName + "\n");
            System.out.println("On port:  " + controlPort + "\n");

            Socket controlSocket = new Socket(serverName, controlPort);

            System.out.println("You are connected to " + serverName + ":" + controlPort);

            while (isOpen && clientgo) {

                DataOutputStream outToServer = new DataOutputStream(controlSocket.getOutputStream());

                DataInputStream inFromServer = new DataInputStream(
                        new BufferedInputStream(controlSocket.getInputStream()));

                sentence = inFromUser.readLine();

                if (sentence.equals("list:")) {

                    port = port + 2;
                    outToServer.writeBytes(port + " " + sentence + " " + '\n');

                    ServerSocket welcomeData = new ServerSocket(port);
                    Socket dataSocket = welcomeData.accept();

                    DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
                    while (notEnd) {
                        modifiedSentence = inData.readUTF();
                        // ........................................
                    }

                    welcomeData.close();
                    dataSocket.close();
                    System.out.println("\nWhat would you like to do next:\n retr: file.txt ||stor: file.txt  || close");
                } else if (sentence.startsWith("RETR ")) {
                    port = port + 2;
                    outToServer.writeBytes(port + " " + sentence + " " + '\n');

                    ServerSocket welcomeData = new ServerSocket(port);
                    Socket dataSocket = welcomeData.accept();
                    DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));

                    // ......................
                    dataSocket.close();
                } else if (sentence.startsWith("STOR ")) {
                    port = port + 2;
                    outToServer.writeBytes(port + " " + sentence + " " + '\n');

                    ServerSocket welcomeData = new ServerSocket(port);
                    Socket dataSocket = welcomeData.accept();
                    DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));

                    // ......................
                    dataSocket.close();
                } else if (sentence.equals("QUIT ")) {
                    isOpen = false;
                    clientgo = false;

                    System.out.println("Client is disconnecting");
                    controlSocket.close();
                } else {
                    System.out
                            .println("\nCommand not recognized try: \n LIST || RETR file.txt ||STOR file.txt  || quit");
                }
            }
        } else {
            System.out.println("\nCommand not recognized:\nPlease CONNECT to a server first.");
        }
    }
}