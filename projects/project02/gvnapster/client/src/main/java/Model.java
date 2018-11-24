import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.nio.file.Path;
import java.util.ArrayList;

public class Model {

    private FTPServer ftpServer;

    private CentralClient centralClient;
    private String serverName;
    private int centralPort;
    private Info clientInfo;
    private Path rootDirPath;


    private ObservableList<FileInfo> obsSearchResults;


    public Model() {
        this.obsSearchResults = FXCollections.observableArrayList();
    }

    public void connect(Info clientInfo, String serverName, int centralPort, Path rootDirPath) {
        this.rootDirPath = rootDirPath;
        this.clientInfo = clientInfo;
        this.centralPort = centralPort;
        this.serverName = serverName;
        this.centralClient = new CentralClient(serverName, centralPort, rootDirPath);

        centralClient.connect();
        centralClient.list(this.clientInfo);

        // Init FTPServer
        this.ftpServer = new FTPServer(this.clientInfo.getPort(), this.rootDirPath);
        // TODO: Probably going to need to make FTPServer a singleton...
    }

    public void search(String searchTerm) {
        ArrayList<FileInfo> searchResults = this.centralClient.search(searchTerm);
        this.obsSearchResults.removeAll();
        this.obsSearchResults = FXCollections.observableArrayList(searchResults);
        this.obsSearchResults.addAll(searchResults);
    }

    public ObservableList<FileInfo> getObsSearchResults() {
        return this.obsSearchResults;
    }

    public void download(FileInfo fileInfo) {
        FTPClient ftpClient = new FTPClient(fileInfo, this.rootDirPath);
    }
}
