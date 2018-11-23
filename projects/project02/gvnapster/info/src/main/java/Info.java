import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;

public class Info implements Serializable {
//    private StringProperty username = new SimpleStringProperty();
//    private StringProperty address = new SimpleStringProperty();
//    private IntegerProperty port = new SimpleIntegerProperty();
//    private StringProperty conType = new SimpleStringProperty();
    private String username;
    private String address;
    private int port;
    private String conType;


    public Info() {}

    public Info(String username, String address, int port, String conType) {
        this.username = username;
        this.address = address;
        this.port = port;
        this.conType = conType;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getConType() {
        return conType;
    }

    public void setConType(String conType) {
        this.conType = conType;
    }
}
