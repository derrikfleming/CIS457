import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

final class CentralServerRequest implements Runnable {
    private Socket socket;
    private Database db;

    private Info userInfo;
    private ArrayList<FileInfo> fileList;
    //streams
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    public CentralServerRequest(Socket socket, Connection conn) throws Exception {
        this.db = new Database(conn);

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
                Object object = objectInputStream.readObject();
                fileList = (ArrayList<FileInfo>)object;
                userInfo = fileList.get(0).getInfo();

                System.out.println("Client '" + userInfo.getUsername() + "' is connected.");
                System.out.println(userInfo.getUsername() + " is sharing the following files: ");
                fileList.forEach((fileInfo -> System.out.println("Filename: " + fileInfo.getFilename())));

                // add new client to DB
                db.newClient(userInfo, fileList);
            }

            // Loop until client disconnects.
            do {
                String searchTerm = null;

                //getting searchterm from client
                Object object;
                try {
                    if ((object = objectInputStream.readObject()) != null) {
                        searchTerm = (String) object;
                    }
                } catch (EOFException e) {
                    break;
                }

                // client disconnected
                if (searchTerm == null || searchTerm.isEmpty()) {
                    break;
                }
                // new download by user
                else if (searchTerm.contains("/*/ Downloaded /*/")) {
                    Object obj = objectInputStream.readObject();
                    ArrayList<FileInfo> newFile = (ArrayList<FileInfo>) obj;

                    int userID = db.getUserID(newFile.get(0).getUsername());
                    db.writeFileList(userID, newFile);
                }
                // client made a search
                else {
                    System.out.print("Received SearchTerm  -> " + searchTerm);
                    System.out.println(", from client user -> " + userInfo.getUsername());

                    //sending search results to centralclient
                    ArrayList<FileInfo> searchResults = search(searchTerm);

                    System.out.println("Sending results back to client -> " + userInfo.getUsername());
                    objectOutputStream.writeObject(searchResults);

                }
            } while (socket.isConnected());
            // Disconnect client and remove client info/filelist from database.
            System.out.println("DISCONNECTED");
            socket.close();
            db.clientDisconnect(userInfo);
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
        ArrayList<FileInfo> results = db.searchFileList(searchTerm, this.userInfo.getUsername());
        return results;
    }


}
