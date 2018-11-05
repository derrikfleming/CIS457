import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

final class CentralServerRequest implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private DataOutputStream out;


    public CentralServerRequest(Socket socket) throws Exception {
        try {
            this.socket = socket;
            out = new DataOutputStream(this.socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void run() {
        try {
            System.out.println("A server thread has started. \nGetting ready to process req's");
            clientHandler();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void clientHandler() {
        if(this.socket.isConnected()){
            System.out.println("Client connected.");
        }
    }

    private void connectDatabase() {

    }
}
