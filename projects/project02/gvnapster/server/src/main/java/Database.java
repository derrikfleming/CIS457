import java.sql.*;
import java.util.ArrayList;

public class Database {

    private Connection conn;
    //private boolean empty = true;

    public Database() {
        connect();
    }

    private void connect() {
        try {
            Class.forName("org.sqlite.JDBC");

            // path relative to module dir
            conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/napster.db");

            //testing
            System.out.println("Connected to Napster DB!");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Add new client info and file list to the database.
     * @param clientInfo Client info.
     * @param clientFileInfos List of files from client.
     */
    public void newClient(Info clientInfo, ArrayList<FileInfo> clientFileInfos) {
        int userID;
        String username = clientInfo.getUsername();
        if(!userExists(username)){
            writeUserData(clientInfo);
            userID = getUserID(username);
            writeFileList(userID, clientFileInfos);
        }
    }

    /**
     * Add new client info and file list to the database.
     * Wrapper to newClient(Info clientInfo, ArrayList<FileInfo> clientFileInfos)
     * @param clientFileInfos List of files from client, along with associated client info.
     */
    public void newClient(ArrayList<FileInfo> clientFileInfos) {
        Info clientInfo = clientFileInfos.get(0).getInfo();
        newClient(clientInfo, clientFileInfos);
    }

    private boolean userExists(String username) {
        boolean exists = false;

        try {
            PreparedStatement ps =
                    conn.prepareStatement( "SELECT * " +
                                                "FROM tblUsers " +
                                                "WHERE username = ?;");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            exists = rs.next();
        } catch (SQLException e) {
            System.out.println(e);
        }

        return exists;
    }

    private void writeUserData(Info clientInfo){
        try {
            PreparedStatement ps =
                    conn.prepareStatement("INSERT INTO tblUsers " +
                                                "VALUES(?,?,?,?,?);");
            ps.setString(1, clientInfo.getUsername());
            ps.setString(2, clientInfo.getAddress());
            ps.setInt(3, clientInfo.getPort());
            ps.setString(4, clientInfo.getConType());
            ps.execute();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void writeFileList(int userID, ArrayList<FileInfo> clientFileInfos) {
        for (int i = 0; i < clientFileInfos.size(); i++) {
            try {
                PreparedStatement ps =
                        conn.prepareStatement("INSERT INTO tblFileList " +
                                                    "VALUES(?,?,?);");
                ps.setInt(1, userID);
                ps.setString(2, clientFileInfos.get(i).getFilename());
                ps.execute();
            } catch (SQLException e) {
                System.out.println(e);
            }
        }
    }

    private int getUserID(String username){
        int userID = Integer.parseInt(null);

        try {
            PreparedStatement ps =
                    conn.prepareStatement( "SELECT * " +
                                                "FROM tblUsers " +
                                                "WHERE username = ?;");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            userID = rs.getInt("id");
        } catch (SQLException e) {
            System.out.println(e);
        }

        return userID;
    }

    public void clientDisconnect (Info clientInfo) {
        int userID = getUserID(clientInfo.getUsername());

        try {
            deleteUser(userID);
        } catch (SQLException e) {
            System.out.println(e);
        }

    }

    private void deleteUser (int userID) throws SQLException {
        PreparedStatement ps =
                conn.prepareStatement( "DELETE " +
                                            "FROM tblUsers " +
                                            "WHERE id = ?;");
        ps.setInt(1, userID);
        ps.executeUpdate();
    }

    /**
     * Search the database for filenames matching searchTerm.
     * @param searchTerm string search for in filenames
     * @return List of files and who has them
     */
    public ArrayList<FileInfo> searchFileList(String searchTerm) {
        ArrayList<FileInfo> results = new ArrayList<>();

        try {
            //getting relevant file listings
            PreparedStatement ps =
                    conn.prepareStatement( "SELECT * " +
                                                "FROM tblFileList " +
                                                "WHERE fileName LIKE ?;");
            ps.setString(1, "%" + searchTerm + '%');

            // records of relevant files
            ResultSet rs1 = ps.executeQuery();


            results = getFileListResults(rs1);
        } catch (SQLException e) {
            System.out.println(e);
        }

        return results;
    }

    /**
     * Build up the ArrayList of FileInfo's from a given query ResultSet.
     * @param fileSet query results
     * @return List of files and who has them
     * @throws SQLException if an exception occurs
     */
    private ArrayList<FileInfo> getFileListResults(ResultSet fileSet) throws SQLException {
        ArrayList<FileInfo> results = new ArrayList<>();

        //getting userdata regarding files
        //iterating through the ResultSet of relevant files
        while (fileSet.next()) {
            FileInfo file = new FileInfo();

            int userID = fileSet.getInt(1);
            String filename = fileSet.getString(2);

            // get the sharing user's info from tblUsers using the userID
            // the current record in tblFileList
            PreparedStatement ps =
                    conn.prepareStatement( "SELECT * " +
                                                "FROM tblUsers " +
                                                "WHERE id = ?;");
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();

            file.setUsername(rs.getString(1));
            file.setAddress(rs.getString(2));
            file.setPort(rs.getInt(3));
            file.setConType(rs.getString(4));
            file.setFilename(filename);

            results.add(file);
        }
        return results;
    }


}
