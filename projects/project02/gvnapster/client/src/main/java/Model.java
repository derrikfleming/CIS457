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
        if (ftpServer == null) {
            this.ftpServer = new FTPServer(this.clientInfo.getPort(), this.rootDirPath);
            // Create a new thread to for the FTPServer.
            Thread thread = new Thread(this.ftpServer);
            // Start the thread.
            thread.start();
        }
    }

    public void search(String searchTerm) {
        ArrayList<FileInfo> searchResults = this.centralClient.search(searchTerm);
        this.obsSearchResults = FXCollections.observableList(searchResults);
    }

    public ObservableList<FileInfo> getObsSearchResults() {
        return this.obsSearchResults;
    }

    public void download(FileInfo fileInfo, FileInfo newSharedInfo) {
        System.out.print("Spawning FTPClient to download '" + fileInfo.getFilename());
        System.out.print("' from " + fileInfo.getAddress() + ":" + fileInfo.getPort() + "\n");
        // Construct an FTPClient to handle the download.
        FTPClient ftpClient = new FTPClient(fileInfo, this.rootDirPath);
        // Create a new thread to process the download.
        Thread thread = new Thread(ftpClient);
        // Start the thread.
        thread.start();

        this.centralClient.newListing(newSharedInfo);
    }
}
