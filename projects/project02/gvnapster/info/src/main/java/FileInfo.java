import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n------ File Information-----\n");
        sb.append("Username: " + getUsername() + "\n");
        sb.append("Address: " + getAddress() + "\n");
        sb.append("Port: " + getPort() + "\n");
        sb.append("ConType: " + getConType() + "\n");
        sb.append("Filename: " + getFilename() + "\n");
        sb.append("*****************************");
        return sb.toString();
    }

    /**
     * Send an ArrayList of FileInfo objects to a socket.
     * @param fout Output socket
     * @param fileInfoArrayList ArrayList of FileInfo objects to send
     */
    public static void sendFileInfoArrayList(Writer fout, ArrayList<FileInfo> fileInfoArrayList) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
            objectMapper.writeValue(fout, fileInfoArrayList);
//            fout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Receive an ArrayList of FileInfo objects from a socket.
     * @param fin Input socket
     * @return ArrayList of FileInfo objects received
     */
    public static ArrayList<FileInfo> recvFileInfoArrayList(Reader fin) {
        ArrayList<FileInfo> fileInfoArrayList = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
            CollectionType javaType = objectMapper.getTypeFactory()
                    .constructCollectionType(ArrayList.class, FileInfo.class);
            fileInfoArrayList = objectMapper.readValue(fin, javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileInfoArrayList;
    }
}
