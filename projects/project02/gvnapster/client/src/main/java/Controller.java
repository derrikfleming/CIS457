import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.ComboBox;

import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        speed.getItems().setAll("T3","T1","Cable","DSL","FM","AM","Millenium Falcon");
    }

    /*** Pane 1 ***********************************************************/
    @FXML TextField serverHostName;
    @FXML TextField port;
    @FXML TextField userName;
    @FXML TextField hostName;
    @FXML ComboBox<String> speed;

    @FXML private void connect(ActionEvent event)
    {
        System.out.println("\n\nConnected to: " + serverHostName.getText() + ", Port: " + port.getText());
        System.out.println("Username: " + userName.getText() + ", Hostname: " + hostName.getText() + ", Speed: " + speed.getValue());
    }
    /*** End of Pane 1 ****************************************************/

    /*** Pane 2 ***********************************************************/
    @FXML TextField keyWord;
    @FXML TableView table;

    @FXML private void search(ActionEvent event)
    {
        
    }
    /*** End of Pane 2 ****************************************************/

    /*** Pane 3 ***********************************************************/
    @FXML TextField command;
    @FXML TextArea commandTextArea;

    @FXML private void executeCommand(ActionEvent event)
    {
        commandTextArea.setText("~: " + command.getText());
    }
    /*** End of Pane 3 ****************************************************/
}
