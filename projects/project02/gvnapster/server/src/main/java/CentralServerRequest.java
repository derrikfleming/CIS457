import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

            // Loop until client disconnects.
            while (!socket.isClosed()) {
                // TODO: wait for search terms from client...
            }

            // Disconnect client and remove client info/filelist from database.
            socket.close();
            db.clientDisconnect(userInfo);

        } catch (Exception e) {
            System.out.println(e);
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
