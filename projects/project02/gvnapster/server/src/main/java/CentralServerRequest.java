import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

final class CentralServerRequest implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private DataOutputStream out;
    private Database db = new Database();

    //userData ArrayList format <username> -> <addr> -> <port> -> <conntype>
    //filelist ArrayList format <filename> -> <filename> -> ...
    private ArrayList<String> userData, fileList;


    public CentralServerRequest(Socket socket) throws Exception {
        try {
            this.socket = socket;
            out = new DataOutputStream(this.socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            // Test CentralServer thread
            System.out.println("CentralServer thread started!");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void run() {
        try {
            System.out.println("A server thread has started. \nGetting ready to process req's");
            clientInitConnect();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void clientInitConnect() {

        if(this.socket.isConnected()){
            System.out.println("Client connected.");

            userData = recvUserData();
            fileList = recvFileList();

            db.newClient(userData, fileList);
        }
    }

    private ArrayList<String> recvUserData() {

        // userData list order:
        //    [username, address, port, connType]
        try {
            for (int i = 0; i < 5; i++) {
                userData.add(in.readLine());
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        return userData;
    }

    private ArrayList<String> recvFileList() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                fileList.add(line);
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        return fileList;
    }

    private ArrayList<String[]> search(String searchTerm) {
        ArrayList<String[]> results = db.searchFileList(searchTerm);

        return results;
    }

    private void disconnectClient() {
        db.clientDisconnect(userData);
    }
}
