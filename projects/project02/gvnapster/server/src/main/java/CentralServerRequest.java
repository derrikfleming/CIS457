import com.sun.xml.internal.bind.v2.TODO;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

final class CentralServerRequest implements Runnable {
    private Socket socket;
//    private BufferedReader in;
//    private DataOutputStream out;
    private Database db = new Database();

    //userData ArrayList format <username> -> <addr> -> <port> -> <conntype>
    //filelist ArrayList format <filename> -> <filename> -> ...
    private Info userInfo;
    private ArrayList<FileInfo> fileList;


    public CentralServerRequest(Socket socket) throws Exception {
        try {
            this.socket = socket;
//            out = new DataOutputStream(this.socket.getOutputStream());
//            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            // Test CentralServer thread
            System.out.println("CentralServer thread started!");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Implement the run() method of the Runnable interface.
     */
    public void run() {
        try {
            System.out.println("A server thread has started.");
            System.out.println("Getting ready to process req's");

            // Initialize client connection
            if(this.socket.isConnected()){
                System.out.println("Client connected.");

                // Receive client's file list.
                fileList = FileInfo.recvFileInfoArrayList(socket);
                userInfo = fileList.get(0).getInfo();

                db.newClient(userInfo, fileList);
            }


            // TODO: double-check this shiiiiiit
            // Loop until client disconnects.
            while (!socket.isClosed()) {
                //getting search term from client
                String searchTerm = getSearchTerm();

                //sending search results to centralclient
                FileInfo.sendFileInfoArrayList(socket, search(searchTerm));
            }

            // Disconnect client and remove client info/filelist from database.
            socket.close();
            db.clientDisconnect(userInfo);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Receives the search term from the client
     * @return the search term
     */
    private String getSearchTerm(){
        String searchTerm = "";
        try(BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
            searchTerm = inFromClient.readLine();
        } catch (IOException e) {
            System.err.println(e);
        }

        return searchTerm;
    }

    /**
     * Search the database for filenames matching searchTerm.
     * @param searchTerm string search for in filenames
     * @return List of files and who has them
     */
    private ArrayList<FileInfo> search(String searchTerm) {
        ArrayList<FileInfo> results = db.searchFileList(searchTerm);
        return results;
    }


}
