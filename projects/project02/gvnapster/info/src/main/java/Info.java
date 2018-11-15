import java.io.Serializable;
import java.util.ArrayList;

public class Info implements Serializable {
    private String username, address, port, conType;

    public Info() {}

    public Info(String username, String address, String port, String conType) {
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

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getConType() {
        return conType;
    }

    public void setConType(String conType) {
        this.conType = conType;
    }
}
