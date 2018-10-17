import java.io.*;
import java.nio.*;
import java.nio.file.*;
// import java.nio.file.Path;
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;

class FTPClient {
    final static String CRLF = "\r\n";

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

    public static String getTokensToString(StringTokenizer tokens) {
        StringBuilder sb = new StringBuilder();
        sb.append(tokens.nextToken());
        while (tokens.hasMoreTokens()) {
            sb.append(" " + tokens.nextToken());
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

        // The root directory for the ftp server.
        Path ftpRootDir = Paths.get("./ftp_client_root_dir/");

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
            // try {
            // controlSocket = new Socket(serverName, controlPort);
            // } catch (Exception e) {
            // System.out.println(e);
            // }

            System.out.println("You are connected to " + serverName + ":" + controlPort);

            while (isOpen && clientgo) {

                outToServer = new DataOutputStream(controlSocket.getOutputStream());

                inFromServer = new DataInputStream(new BufferedInputStream(controlSocket.getInputStream()));

                sentence = inFromUser.readLine();

                if (sentence.equals("LIST")) {
                    dataPort = dataPort + 2;
                    System.out.println("writing command to server: " + dataPort + " " + sentence);

                    ServerSocket server = new ServerSocket(dataPort);
                    outToServer.writeBytes(dataPort + " " + sentence + " " + CRLF);
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
                    // System.out.println("datasocket is closed");
                    System.out.println("\nWhat would you like to do next:\n retr: file.txt ||stor: file.txt  || close");

                } else if (sentence.startsWith("RETR ")) {
                    dataPort = dataPort + 2;

                    ServerSocket server = new ServerSocket(dataPort);
                    System.out.println("writing command to server: " + dataPort + " " + sentence);
                    outToServer.writeBytes(dataPort + " " + sentence + " " + CRLF);
                    Socket dataSocket = server.accept();
                    String[] comTokens = sentence.split("\\s+");
                    String fileName = comTokens[1];
                    DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));

                    try {
                        StringBuffer sb = new StringBuffer();
                        String line;
                        while (inData.available() > 0)
                            sb.append(inData.readUTF());

                        Path path = Paths.get("./ftp_client_root_dir/" + fileName);
                        Files.write(path, sb.toString().getBytes());
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    inData.close();
                    dataSocket.close();
                } else if (sentence.startsWith("STOR ")) {
                    dataPort = dataPort + 2;
                    outToServer.writeBytes(dataPort + " " + sentence + " " + CRLF);

                    ServerSocket server = new ServerSocket(dataPort);
                    Socket dataSocket = server.accept();
                    DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));

                    server.close();
                    dataSocket.close();
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
