import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.nio.file.Path;
import java.util.ArrayList;

public class Model {

    CentralClient centralClient;
    String serverName;
    int centralPort;
    Info clientInfo;
    FileInfo clientFileInfo;


    ObservableList<FileInfo> obsSearchResults;


    public Model() {

    }

    public void connect(Info clientInfo, String serverName, int centralPort, Path rootDirPath) {
        this.clientInfo = clientInfo;
        this.centralPort = centralPort;
        this.serverName = serverName;
        this.centralClient = new CentralClient(serverName, centralPort, rootDirPath);

        centralClient.connect();
        centralClient.list(this.clientInfo);
    }

    public void search (String searchTerm) {
        ArrayList<FileInfo> searchResults = this.centralClient.search(searchTerm);

        if(!this.obsSearchResults.isEmpty()){
            this.obsSearchResults = FXCollections.observableArrayList(searchResults);
        } else {
            obsSearchResults.removeAll();
            obsSearchResults.addAll(searchResults);
        }
    }

    public ObservableList<FileInfo> getObsSearchResults() {
        return this.obsSearchResults;
    }
}
