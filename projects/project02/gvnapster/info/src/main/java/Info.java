import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Info implements Serializable {
    private String username;
    private InetAddress address;
    private int port;
    private String conType;

    public Info() {}

    public Info(String username, InetAddress address, int port, String conType) {
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

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public String getHostAddress() {
        return address.getHostAddress();
    }

    public void setAddress(String address) {
        try {
            this.address = (InetAddress.getByName(address));
        } catch (UnknownHostException e) {
            System.err.println(e);
        }
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
