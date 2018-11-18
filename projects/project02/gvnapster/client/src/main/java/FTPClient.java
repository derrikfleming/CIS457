import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.nio.charset.*;
import static java.nio.file.StandardOpenOption.*;
import java.net.*;
import java.util.*;

import java.text.*;
import java.lang.*;

class FTPClient {
    final static String CRLF = "\r\n";

    /**
     * FTPClient main()
     * @param  argv[]    CLI arguments.
     * @throws Exception If an error occurs.
     */
    public static void main(String argv[]) throws Exception {
        String sentence;
        String modifiedSentence;
        boolean isOpen = true;
        int number = 1;

        Socket controlSocket;
        DataOutputStream outToServer;
        DataInputStream inFromServer;
        int controlPort, dataPort;
        // The root directory for the ftp server.
        Path ftpRootDir = Paths.get("./ftp_client_root_dir");

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

            System.out.println("You are connecting to " + serverName);
            System.out.println("On dataPort:  " + controlPort);

            controlSocket = new Socket(serverName, controlPort);
            System.out.println("You are connected to " + serverName + ":" + controlPort);

            while (isOpen && clientgo) {
                outToServer = new DataOutputStream(controlSocket.getOutputStream());
                inFromServer = new DataInputStream(new BufferedInputStream(controlSocket.getInputStream()));
                sentence = inFromUser.readLine();

                if (sentence.equals("LIST")) {
                    dataPort = dataPort + 2;

                    ServerSocket server = new ServerSocket(dataPort);
                    System.out.println("writing command to server: " + dataPort + " " + sentence);
                    outToServer.writeBytes(dataPort + " " + sentence + " " + CRLF);
                    Socket dataSocket = server.accept();

                    try (DataInputStream inData = new DataInputStream(
                            new BufferedInputStream(dataSocket.getInputStream()))) {

                        modifiedSentence = FTPHelper.getUTF8String(inData);
                        System.out.println(modifiedSentence);

                    } catch (IOException e) {
                        // Output unexpected IOExceptions.
                        System.out.println(e);
                    }

                    dataSocket.close();
                    server.close();
                    System.out.println("datasocket is closed");
                    System.out.println("\nWhat would you like to do next:\n retr: name.txt ||stor: file.txt  || close");

                } else if (sentence.startsWith("RETR ")) {
                    // Retrieve a file from the server
                    dataPort = dataPort + 2;

                    StringTokenizer commandTokens = new StringTokenizer(sentence);
                    String clientCommand = commandTokens.nextToken();
                    String filename = FTPHelper.getTokensToString(commandTokens);

                    try (ServerSocket server = new ServerSocket(dataPort)) {
                        // System.out.println("writing command to server:\n" + dataPort + " " + sentence);
                        outToServer.writeBytes(dataPort + " " + sentence + " " + CRLF);

                        try (Socket dataSocket = server.accept()) {
                            Path file = Paths.get(ftpRootDir + "/" + filename);
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

                } else if (sentence.startsWith("STOR ")) {
                    // Store a file on the server
                    dataPort = dataPort + 2;

                    StringTokenizer commandTokens = new StringTokenizer(sentence);
                    String clientCommand = commandTokens.nextToken();
                    String filename = FTPHelper.getTokensToString(commandTokens);

                    try (ServerSocket server = new ServerSocket(dataPort)) {
                        // System.out.println("writing command to server:\n" + dataPort + " " + sentence);
                        outToServer.writeBytes(dataPort + " " + sentence + " " + CRLF);

                        try (Socket dataSocket = server.accept()) {
                            Path file = Paths.get(ftpRootDir + "/" + filename);
                            // Store a file on the server
                            FTPHelper.sendFile(dataSocket, file);
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

                } else if (sentence.equals("QUIT")) {
                    isOpen = false;
                    clientgo = false;
                    outToServer.writeBytes(dataPort + " " + sentence + " " + CRLF);
                    System.out.println("Client is disconnecting");
                    controlSocket.close();
                } else {
                    outToServer.writeBytes("");
                    System.out.println("\nCommand not recognized\n");
                }
            }
        } else {
            System.out.println("\nCommand not recognized:\nPlease CONNECT to a server first.");
        }
    }
}
