import java.io.Serializable;
import java.util.ArrayList;

public class FileInfo implements Serializable {
    private Info info;
    private String filename;

    public FileInfo(Info info, String filename) {
        this.info = info;
        this.filename = filename;
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
}
