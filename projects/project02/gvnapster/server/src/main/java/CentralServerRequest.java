import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

final class CentralServerRequest implements Runnable {
    private Socket socket;
    private Database db = new Database();

    private Info userInfo;
    private ArrayList<FileInfo> fileList;
    //streams
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    public CentralServerRequest(Socket socket) throws Exception {
        try {
            this.socket = socket;

            //out
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            //in
            objectInputStream = new ObjectInputStream(socket.getInputStream());

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
            System.out.println("Getting ready to process req's");

            // Initialize client connection
            if(socket.isConnected()){
                System.out.println("Client connected.");
                // Receive client's file list.
//                fileList = FileInfo.recvFileInfoArrayList(fin);

                Object object = objectInputStream.readObject();
                fileList = (ArrayList<FileInfo>)object;

                System.out.println(fileList.toString());
                userInfo = fileList.get(0).getInfo();
                db.newClient(userInfo, fileList);
            }

            System.out.println("Socket.isClosed @ pre-searchloop -> " + socket.isClosed());
            System.out.println("Before search loop");

            // TODO: double-check this shiiiiiit
            // Loop until client disconnects.
            while (socket.isConnected()) {
//            while (!socket.isClosed()) {
                System.out.println("Begin search loop");

                //getting search term from client
                Object object = objectInputStream.readObject();
                String searchTerm = (String) object;

                if (searchTerm == null || searchTerm.isEmpty()) {
                    break;
                } else {
                    System.out.println("SearchTerm  -> " + searchTerm);
                    //sending search results to centralclient
                    ArrayList<FileInfo> searchResults = search(searchTerm);

                    objectOutputStream.writeObject(searchResults);

//                    FileInfo.sendFileInfoArrayList(fout, searchResults);
                }
            }
            System.out.println("After search loop");

            // Disconnect client and remove client info/filelist from database.
            socket.close();

            // Testing db.searchFileList()...
            System.out.println("Before testing db.searchFileList()");
            ArrayList<FileInfo> tempSearch = db.searchFileList("UTF");
            System.out.println("After testing db.searchFileList()");
            System.out.println("Before db.clientDisconnect()");
            db.clientDisconnect(userInfo);
            System.out.println("After db.clientDisconnect()");

        } catch (Exception e) {
            e.printStackTrace();
        }
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
