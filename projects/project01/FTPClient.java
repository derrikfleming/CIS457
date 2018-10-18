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
     * Stringifies an InputStream with UTF-8 encoding
     * @param  in          InputStream to Stringify.
     * @return             A String.
     * @throws IOException If an I/O error occurs.
     */
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

    /**
     * Gets a String consisting of all remaining tokens appended sequentially.
     * @param  tokens StringTokenizer containing tokens.
     * @return        Single String consisting of all remaining tokens appended sequentially.
     */
    public static String getTokensToString(StringTokenizer tokens) {
        StringBuilder sb = new StringBuilder();
        sb.append(tokens.nextToken());
        while (tokens.hasMoreTokens()) {
            sb.append(" " + tokens.nextToken());
        }
        return sb.toString();
    }

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

                        modifiedSentence = getUTF8String(inData);
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
                    dataPort = dataPort + 2;

                    StringTokenizer commandTokens = new StringTokenizer(sentence);
                    String clientCommand = commandTokens.nextToken();
                    String fileName = getTokensToString(commandTokens);

                    ServerSocket server = new ServerSocket(dataPort);
                    System.out.println("writing command to server:\n" + dataPort + " " + sentence);
                    outToServer.writeBytes(dataPort + " " + sentence + " " + CRLF);
                    Socket dataSocket = server.accept();

                    try {
                        Reader reader = new InputStreamReader(dataSocket.getInputStream());
                        BufferedReader fin = new BufferedReader(reader);
                        Writer writer = new OutputStreamWriter(
                                new FileOutputStream(new File(ftpRootDir.toString() + "/" + fileName)), "UTF-8");
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
                    server.close();
                    System.out.println("datasocket is closed");
                } else if (sentence.startsWith("STOR ")) {
                    dataPort = dataPort + 2;
                    ServerSocket server = new ServerSocket(dataPort);
                    StringTokenizer commandTokens = new StringTokenizer(sentence);
                    String clientCommand = commandTokens.nextToken();
                    String fileName = getTokensToString(commandTokens);
                    outToServer.writeBytes(dataPort + " " + sentence + " " + CRLF);

                    Socket dataSocket = server.accept();

                    try {
                        DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

                        Path file = Paths.get(ftpRootDir + "/" + fileName);

                        try {
                            Reader reader = new InputStreamReader(new FileInputStream(file.toString()));
                            BufferedReader fin = new BufferedReader(reader);
                            Writer writer = new OutputStreamWriter(dataOutToClient, "UTF-8");
                            BufferedWriter fout = new BufferedWriter(writer);
                            String s;
                            boolean firstline = true;
                            while ((s = fin.readLine()) != null) {
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
