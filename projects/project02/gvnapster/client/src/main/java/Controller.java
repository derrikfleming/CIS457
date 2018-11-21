import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable
{

    CentralClient centralClient;
    String serverName;
    String username;
    int serverPort;
    int hostPort;
    Info clientInfo;

    Model model;

    private ObservableList<FileInfo> fileInfoList = FXCollections.observableArrayList();

    private Path rootDirPath;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        if(this.model == null){
            this.model = new Model();
        }

        speed.getItems().setAll("T3","T1","Cable","DSL","FM","AM","Millenium Falcon");

        // Bind FileInfo fields to the table cols.
        speedColumn.setCellValueFactory(param -> param.getValue().conTypeProperty());
        table.getColumns().add(speedColumn);

        hostNameColumn.setCellValueFactory(param -> param.getValue().addressProperty());
        table.getColumns().add(hostNameColumn);

        fileNameColumn.setCellValueFactory(param -> param.getValue().filenameProperty());
        table.getColumns().add(fileNameColumn);

        fileInfoList.addListener((ListChangeListener<FileInfo>) c -> c.getList());

    }


    /*** ... initialized **************************************************/
    ////////////////////////////////////////////////////////////////////////
    /*** Pane 1 ***********************************************************/
    
    //CentralServer Infos
    @FXML TextField serverNameField;
    @FXML TextField serverPortField;

    //Client/host Infos
    @FXML TextField userNameField;
    @FXML TextField hostNameField;
    @FXML TextField hostPortField;
    @FXML TextField rootDirField;
    @FXML ComboBox<String> speed;

    @FXML private void connect(ActionEvent event)
    {
        // set vars with field data
        serverName = serverNameField.getText();
        rootDirPath = Paths.get(rootDirField.getText());
        serverPort = Integer.parseInt(serverPortField.getText());
        hostPort = Integer.parseInt(hostPortField.getText());
        username = userNameField.getText();

        clientInfo = new Info(userNameField.getText(), hostNameField.getText(), Integer.parseInt(hostPortField.getText()), speed.getValue());

        //connect on the model
        model.connect(clientInfo, serverName, serverPort, rootDirPath);
    }
    /*** End of Pane 1 ****************************************************/
    ////////////////////////////////////////////////////////////////////////
    /*** Pane 2 ***********************************************************/

    @FXML TextField searchTermField;
    @FXML TableView<FileInfo> table;
    TableColumn<FileInfo, String> speedColumn = new TableColumn<FileInfo, String>("Speed");
    TableColumn<FileInfo, String> hostNameColumn = new TableColumn<FileInfo, String>("Hostname");
    TableColumn<FileInfo, String> fileNameColumn = new TableColumn<FileInfo, String>("Filename");

    @FXML private void search(ActionEvent event) throws Exception
    {
        String searchTerm = searchTermField.getText();

        if (searchTerm != null && !searchTerm.isEmpty()) {
            this.model.search(searchTerm);
            this.fileInfoList = this.model.getObsSearchResults();
            table.refresh();
        }
    }

    @FXML private void download(ActionEvent event) throws Exception
    {
        FileInfo selected = table.getSelectionModel().getSelectedItem();

        System.out.println(selected.getFilename());
        // TODO: spawn FTPClient
    }

}

