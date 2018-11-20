import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ComboBox;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    /*** ... initialized **************************************************/
    ////////////////////////////////////////////////////////////////////////
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
    ////////////////////////////////////////////////////////////////////////
    /*** Pane 2 ***********************************************************/

    @FXML TextField keyWord;
    @FXML TableView table;
    TableColumn<String, String> speedColumn = new TableColumn<>("Speed");
    TableColumn<String, String> hostNameColumn = new TableColumn<>("Hostname");
    TableColumn<String, String> fileNameColumn = new TableColumn<>("Filename");

    private ObservableList<String> getData()
    {
        ObservableList<String> data = FXCollections.observableArrayList();

        data.add("one");
        data.add("two");
        data.add("three");
        
        return data;
    }

    @FXML private void search(ActionEvent event) throws Exception
    {
        speedColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue()));
        hostNameColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue()));
        fileNameColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue()));

        table.getColumns().add(speedColumn);
        table.getColumns().add(hostNameColumn);
        table.getColumns().add(fileNameColumn);

        table.setItems(getData());
    }

    /*** End of Pane 2 ****************************************************/
    ////////////////////////////////////////////////////////////////////////
    /*** Pane 3 ***********************************************************/

    @FXML TextField command;
    @FXML TextArea commandTextArea;

    @FXML private void executeCommand(ActionEvent event)
    {
        commandTextArea.setText("~: " + command.getText());
    }

    /*** End of Pane 3 ****************************************************/
}

