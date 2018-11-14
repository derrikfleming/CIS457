import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

public class FileInfo implements Serializable {
    private Info info;
    private String filename;

    public FileInfo(Info info, String filename) {
        this.info = info;
        this.filename = filename;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public String getUsername() {
        return info.getUsername();
    }

    public void setUsername(String username) {
        info.setUsername(username);
    }

    public String getAddress() {
        return info.getAddress();
    }

    public void setAddress(String address) {
        info.setAddress(address);
    }

    public String getPort() {
        return info.getPort();
    }

    public void setPort(String port) {
        info.setPort(port);
    }

    public String getConType() {
        return info.getConType();
    }

    public void setConType(String conType) {
        info.setConType(conType);
    }

    public String getFilename() {
        return filename;
    }

    public void setfilename(String filename) {
        this.filename = filename;
    }
    
    /**
     * Send an ArrayList of FileInfo objects to a socket.
     * @param out Output socket
     * @param fileInfoArrayList ArrayList of FileInfo objects to send
     */
    public static void sendFileInfoArrayList(Socket out, ArrayList<FileInfo> fileInfoArrayList) {
        try (
                DataOutputStream dataOut = new DataOutputStream(out.getOutputStream());
                ObjectOutputStream objectOut = new ObjectOutputStream(dataOut);
        ) {
            objectOut.writeObject(fileInfoArrayList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recieve an ArrayList of FileInfo objects from a socket.
     * @param in Input socket
     * @return ArrayList of FileInfo objects recieved
     */
    public static ArrayList<FileInfo> recvFileInfoArrayList(Socket in) {
        ArrayList<FileInfo> fileInfoArrayList = new ArrayList<>();
        try (ObjectInputStream objectIn = new ObjectInputStream(in.getInputStream())) {
            try {
                Object object = objectIn.readObject();
                fileInfoArrayList = (ArrayList<FileInfo>) object;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileInfoArrayList;
    }
}
