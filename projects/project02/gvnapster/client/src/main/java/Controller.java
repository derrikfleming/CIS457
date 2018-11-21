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
import java.net.InetAddress;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.google.common.net.InetAddresses;

public class Controller implements Initializable
{

    CentralClient centralClient;
    String serverName;
    int centralPort;
    Info clientInfo;
    FileInfo clientFileInfo;

    private final ObservableList<FileInfo> fileInfoList = FXCollections.observableArrayList();

    private Path rootDirPath;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        speed.getItems().setAll("T3","T1","Cable","DSL","FM","AM","Millenium Falcon");

        // Bind FileInfo fields to the table cols.
        speedColumn.setCellValueFactory(param -> param.getValue().conTypeProperty());
        table.getColumns().add(speedColumn);

        hostNameColumn.setCellValueFactory(param -> param.getValue().addressProperty());
        table.getColumns().add(hostNameColumn);

        fileNameColumn.setCellValueFactory(param -> param.getValue().filenameProperty());
        table.getColumns().add(fileNameColumn);
    }

    /*** ... initialized **************************************************/
    ////////////////////////////////////////////////////////////////////////
    /*** Pane 1 ***********************************************************/
    
    //CentralServer Infos
    @FXML TextField serverHostName;
    @FXML TextField serverPort;

    //Client/host Infos
    @FXML TextField userName;
    @FXML TextField hostName;
    @FXML TextField hostPort;
    @FXML TextField rootDir;
    @FXML ComboBox<String> speed;

    @FXML private void connect(ActionEvent event)
    {
        System.out.println("\n\nConnected to: " + serverHostName.getText() + ", Server Port: " + serverPort.getText() + ", Host Port: " + hostPort.getText() + ", Root Dir: " + rootDir.getText());
        System.out.println("Username: " + userName.getText() + ", Hostname: " + hostName.getText() + ", Speed: " + speed.getValue());

        centralPort = Integer.parseInt(serverPort.getText());

        clientInfo = new Info(userName.getText(), hostName.getText(), Integer.parseInt(hostPort.getText()), speed.getValue());

        rootDirPath = Paths.get(rootDir.getText());

        centralClient = new CentralClient(serverHostName.getText(), centralPort, rootDirPath);

        centralClient.connect();

        centralClient.list(clientInfo);
    }
    /*** End of Pane 1 ****************************************************/
    ////////////////////////////////////////////////////////////////////////
    /*** Pane 2 ***********************************************************/

    @FXML TextField keyWord;
    @FXML TableView<FileInfo> table;
    TableColumn<FileInfo, String> speedColumn = new TableColumn<FileInfo, String>("Speed");
    TableColumn<FileInfo, String> hostNameColumn = new TableColumn<FileInfo, String>("Hostname");
    TableColumn<FileInfo, String> fileNameColumn = new TableColumn<FileInfo, String>("Filename");

    private ObservableList<FileInfo> getData()
    {
        ArrayList<FileInfo> dataArray = new ArrayList<FileInfo>();

        Info myInfo = new Info("aldunc", "als address", 1234, "millenium falcon");
        Info myInfo2 = new Info("derrik", "derriks address", 1234, "millenium falcon");
        Info myInfo3 = new Info("joe", "joe's address", 1234, "millenium falcon");

        FileInfo myFileInfo = new FileInfo( myInfo, "blah1.txt");
        FileInfo myFileInfo2 = new FileInfo( myInfo2, "blah2.txt");
        FileInfo myFileInfo3 = new FileInfo( myInfo3, "blah3.txt");

        dataArray.add(myFileInfo);
        dataArray.add(myFileInfo2);
        dataArray.add(myFileInfo3);
        
        ObservableList<FileInfo> data = FXCollections.observableArrayList(dataArray);
        
        return data;
    }

    @FXML private void search(ActionEvent event) throws Exception
    {
        String searchTerm = keyWord.getText();

        if (searchTerm != null && !searchTerm.isEmpty()) {
            ArrayList<FileInfo> searchResults = centralClient.search(searchTerm);

//            ObservableList<FileInfo> data = FXCollections.observableArrayList(searchResults);


//            table.setItems(getData());

            // Clear the fileInfoList, then add the new searchResults
            fileInfoList.removeAll(fileInfoList);
            fileInfoList.addAll(searchResults);

            table.refresh();
        }
    }

    @FXML private void download(ActionEvent event) throws Exception
    {
        FileInfo selected = table.getSelectionModel().getSelectedItem();

        System.out.println(selected.getFilename());
        // TODO: spawn FTPClient
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

