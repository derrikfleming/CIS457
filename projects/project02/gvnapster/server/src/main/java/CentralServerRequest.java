import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
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
    //reader/writer
    private BufferedReader fin;
    private BufferedWriter fout;


    public CentralServerRequest(Socket socket) throws Exception {
        try {
            this.socket = socket;
//            out = new DataOutputStream(this.socket.getOutputStream());
//            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            //out
            DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
            Writer writer = new OutputStreamWriter(dataOut, "UTF-8");
            this.fout = new BufferedWriter(writer);

            //in
            Reader reader = new InputStreamReader(socket.getInputStream());
            this.fin = new BufferedReader(reader);

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
            if(socket.isConnected()){
                System.out.println("Client connected.");

                // Receive client's file list.
                fileList = FileInfo.recvFileInfoArrayList(fin);

                System.out.println(fileList);
                fileList.forEach(fileInfo -> {
//                    System.out.println(fileInfo.getAddress());
//                    System.out.println(fileInfo.getPort());
                    System.out.println(fileInfo.getFilename());
                });

                userInfo = fileList.get(0).getInfo();

                db.newClient(userInfo, fileList);

            }
            System.out.println("Socket.isClosed @ pre-searchloop -> " + socket.isClosed());
            System.out.println("Before search loop");
            // TODO: double-check this shiiiiiit
            // Loop until client disconnects.
//            while (socket.isConnected()) {
            while (!socket.isClosed()) {
                System.out.println("Begin search loop");
                //getting search term from client
                fileList = FileInfo.recvFileInfoArrayList(fin);

                System.out.println(fileList);

                //sending search results to centralclient
//                FileInfo.sendFileInfoArrayList(socket, search(searchTerm));
            }
            System.out.println("After search loop");

            // Disconnect client and remove client info/filelist from database.
            socket.close();
            // TODO: FIX this shiiiiiit.
            ArrayList<FileInfo> tempSearch = db.searchFileList("UTF");
            tempSearch.forEach(fileInfo -> {System.out.println(fileInfo.getFilename());});
//            db.clientDisconnect(userInfo);

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
        try {
            Reader reader = new InputStreamReader(socket.getInputStream());
            BufferedReader fin = new BufferedReader(reader);
            searchTerm = fin.readLine();
            System.out.println("getSearchTerm() ->" + searchTerm);
        } catch (IOException e) {
            e.printStackTrace();
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
