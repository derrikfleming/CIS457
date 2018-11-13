import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.nio.charset.*;
import static java.nio.file.StandardOpenOption.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;

/**
 * FTPHelper
 */
public class FTPHelper {

    /**
     * Stringifies an InputStream with UTF-8 encoding
     * @param in InputStream to Stringify.
     * @return A String.
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
     * @param tokens StringTokenizer containing tokens.
     * @return Single String consisting of all remaining tokens appended
     *         sequentially.
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
     * Read line-by-line from reader to writer.
     * @param  fin  [description]
     * @param  fout [description]
     * @throws IOException If an I/O error occurs.
     */
    public static void readIntoWriter(BufferedReader fin, BufferedWriter fout) throws IOException {
        String s;
        boolean firstline = true;
        while ((s = fin.readLine()) != null) {
            // System.out.println(s);
            if (!firstline)
                fout.newLine();
            else
                firstline = false;
            fout.write(s);
        }
    }

    /**
     * Send file to a socket.
     * @param out  Output socket
     * @param file Input file
     */
    public static void sendFile(Socket out, Path file) {
        try (
                Reader reader = new InputStreamReader(new FileInputStream(file.toString()));
                BufferedReader fin = new BufferedReader(reader);
                DataOutputStream dataOut = new DataOutputStream(out.getOutputStream());
                Writer writer = new OutputStreamWriter(dataOut, "UTF-8");
                BufferedWriter fout = new BufferedWriter(writer);
        ) {
            readIntoWriter(fin, fout);
            fin.close();
            fout.close();
            // out.close();
            // System.out.println("Data Socket closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recieve file from socket.
     * @param in   Input socket
     * @param file Output file
     */
    public static void recvFile(Socket in, Path file) {
        try (
                Reader reader = new InputStreamReader(in.getInputStream());
                BufferedReader fin = new BufferedReader(reader);
                Writer writer = new OutputStreamWriter(new FileOutputStream(new File(file.toString())), "UTF-8");
                BufferedWriter fout = new BufferedWriter(writer);
        ) {
            readIntoWriter(fin, fout);
            fin.close();
            fout.close();
            // in.close();
            // System.out.println("datasocket is closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // public static Path getFilePath(String filename) {
    //     // TODO
    // }



    /**
     * List all files on the server
     * @param out Output socket
     * @param dir Directory to list files from
     * @param hostInfo Host Info for creating FileInfo
     */
    public static void list(Socket out, Path dir, Info hostInfo) {
        try (DataOutputStream dataOutToClient = new DataOutputStream(out.getOutputStream())) {
            ArrayList<FileInfo> files = new ArrayList<>();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
//                stream.forEach(file -> {files.add(file)});

                for (Path file : stream) {
                    files.add(new FileInfo(hostInfo, file.getFileName().toString()));
//                    dataOutToClient.writeUTF(file.getFileName().toString() + CRLF);
                    // System.out.println(file.getFileName().toString());
                }
            } catch (IOException | DirectoryIteratorException x) {
                // IOException can never be thrown by the iteration.
                // In this snippet, it can only be thrown by newDirectoryStream.
                System.err.println(x);
            }
            dataOutToClient.flush();
//            out.close();
            // System.out.println("Data Socket closed");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // public static void retr(Socket ) {
    //     // TODO
    // }

}
