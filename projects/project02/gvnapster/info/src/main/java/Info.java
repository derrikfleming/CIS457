import java.io.Serializable;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Info implements Serializable {
    private StringProperty username = new SimpleStringProperty();
    private StringProperty address = new SimpleStringProperty();
    private IntegerProperty port = new SimpleIntegerProperty();
    private StringProperty conType = new SimpleStringProperty();

    public Info() {}

    public Info(String username, String address, int port, String conType) {
        setUsername(username);
        setAddress(address);
        setPort(port);
        setConType(conType);
    }

    public StringProperty usernameProperty() {
        return this.username;
    }

    public String getUsername() {
        return usernameProperty().get();
    }

    public void setUsername(String username) {
        this.usernameProperty().set(username);
    }

    public StringProperty addressProperty() {
        return this.address;
    }

    public String getAddress() {
        return addressProperty().get();
    }

    public void setAddress(String address) {
        this.addressProperty().set(address);
    }

    public IntegerProperty portProperty() {
        return this.port;
    }

    public int getPort() {
        return portProperty().get();
    }

    public void setPort(int port) {
        this.portProperty().set(port);
    }

    public StringProperty conTypeProperty() {
        return this.conType;
    }

    public String getConType() {
        return conTypeProperty().get();
    }

    public void setConType(String conType) {
        this.conTypeProperty().set(conType);
    }
}
