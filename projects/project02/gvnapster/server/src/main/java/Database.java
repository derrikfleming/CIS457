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
            ps.setString(2, clientInfo.getHostAddress());
            ps.setString(3, clientInfo.getConType());
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

    // TODO: Modify to return ArrayList<FileInfo>
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


            results = getUserData(rs1);
        } catch (SQLException e) {
            System.out.println(e);
        }

        return results;
    }

    // TODO: Modify to return ArrayList<FileInfo>
    private ArrayList<FileInfo> getUserData(ResultSet fileSet) throws SQLException {
        ArrayList<FileInfo> results = new ArrayList<>();

        //getting userdata regarding files
        //iterating through the ResultSet of relevant files
        while (fileSet.next()){
            FileInfo file = new FileInfo();
            String[] record = new String[4];
            int userID = fileSet.getInt(2);

            // get the sharing user's info from tblUsers using the userID
            // the current record in tblFileList
            PreparedStatement ps =
                    conn.prepareStatement( "SELECT id, connType, address, port " +
                                                "FROM tblUsers " +
                                                "WHERE id = ?;");
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();

            // add connType, then address, then port to record[]
            for (int i = 0, j = 1; i < 3; i++, j++){
                record[i] = rs.getString(j);
            }
            // TODO: I'm unsure of the exact schema that rs will return.
            // TODO: Need help parsing rs into FileInfo instance.
            file.setUsername(rs.getString(1));
            file.setAddress(rs.getString(2));
            file.setConType(rs.getString(3));

            // add filename to record[]
            record[3] = fileSet.getString("filename");

            results.add(file);
        }
        return results;
    }


}
