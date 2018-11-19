import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FileInfo {
    private Info info;
    private StringProperty filename = new SimpleStringProperty();

    public FileInfo() {
    }

    public FileInfo(Info info, String filename) {
        this.info = info;
        setFilename(filename);
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public StringProperty usernameProperty() {
        return this.info.usernameProperty();
    }

    public String getUsername() {
        return info.getUsername();
    }

    public void setUsername(String username) {
        info.setUsername(username);
    }

    public StringProperty addressProperty() {
        return this.info.addressProperty();
    }

    public String getAddress() {
        return info.getAddress();
    }

    public void setAddress(String address) {
        info.setAddress(address);
    }

    public IntegerProperty portProperty() {
        return this.info.portProperty();
    }

    public int getPort() {
        return info.getPort();
    }

    public void setPort(int port) {
        info.setPort(port);
    }

    public StringProperty conTypeProperty() {
        return this.info.conTypeProperty();
    }

    public String getConType() {
        return info.getConType();
    }

    public void setConType(String conType) {
        info.setConType(conType);
    }

    public StringProperty filenameProperty() {
        return this.filename;
    }

    public String getFilename() {
        return this.filenameProperty().get();
    }

    public void setFilename(String filename) {
        this.filenameProperty().set(filename);
    }

    /**
     * Send an ArrayList of FileInfo objects to a socket.
     * @param out Output socket
     * @param fileInfoArrayList ArrayList of FileInfo objects to send
     */
    public static void sendFileInfoArrayList(Socket out, ArrayList<FileInfo> fileInfoArrayList) {
        try (
                DataOutputStream dataOut = new DataOutputStream(out.getOutputStream());
                Writer writer = new OutputStreamWriter(dataOut, "UTF-8");
                BufferedWriter fout = new BufferedWriter(writer);
        ) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(fout, fileInfoArrayList);
//            String json = objectMapper.writeValueAsString(fileInfoArrayList);
//            dataOut.writeUTF(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Receive an ArrayList of FileInfo objects from a socket.
     * @param in Input socket
     * @return ArrayList of FileInfo objects received
     */
    public static ArrayList<FileInfo> recvFileInfoArrayList(Socket in) {
        ArrayList<FileInfo> fileInfoArrayList = new ArrayList<>();
        try (
                Reader reader = new InputStreamReader(in.getInputStream());
                BufferedReader fin = new BufferedReader(reader);
        ) {
            ObjectMapper objectMapper = new ObjectMapper();

            CollectionType javaType = objectMapper.getTypeFactory()
                    .constructCollectionType(ArrayList.class, FileInfo.class);

            fileInfoArrayList = objectMapper.readValue(fin, javaType);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileInfoArrayList;
    }
}
