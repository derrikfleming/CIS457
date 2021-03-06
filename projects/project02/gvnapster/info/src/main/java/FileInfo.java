//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.type.CollectionType;
//import com.google.common.reflect.TypeToInstanceMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.hildan.fxgson.FxGson;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FileInfo implements Serializable {
    private Info info;
//    private StringProperty filename = new SimpleStringProperty();
private String filename;
    public FileInfo() {
    }



    public FileInfo(Info info, String filename) {
        this.info = info;
//        setFilename(filename);
        this.filename = filename;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUsername() {
        return this.info.getUsername();
    }

    public void setUsername(String username) {
        this.info.setUsername(username);
    }

    public String getAddress() {
        return this.info.getAddress();
    }

    public void setAddress(String address) {
        this.info.setAddress(address);
    }

    public int getPort() {
        return this.info.getPort();
    }

    public void setPort(int port) {
        this.info.setPort(port);
    }

    public String getConType() {
        return this.info.getConType();
    }

    public void setConType(String conType) {
        this.info.setConType(conType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n------ File Information-----\n");
        sb.append("Username: " + this.info.getUsername() + "\n");
        sb.append("Address: " + this.info.getAddress() + "\n");
        sb.append("Port: " + this.info.getPort() + "\n");
        sb.append("ConType: " + this.info.getConType() + "\n");
        sb.append("Filename: " + getFilename() + "\n");
        sb.append("*****************************\n");
        return sb.toString();
    }
}
