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

    public void newClient(ArrayList<FileInfo> clientFileInfos){
        int userID;
        Info clientInfo = clientFileInfos.get(0).getInfo();
        String username = clientInfo.getUsername();
        if(!userExists(username)){
            writeUserData(clientInfo);
            userID = getUserID(username);
            writeFileList(userID, clientFileInfos);
        }
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

    public void clientDisconnect (ArrayList<FileInfo> clientFileInfos) {
        int userID = getUserID(clientFileInfos.get(0).getInfo().getUsername());

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

    public ArrayList<String[]> searchFileList(String searchTerm) {
        ArrayList<String[]> results = new ArrayList<String[]>();

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

    private ArrayList<String[]> getUserData(ResultSet fileSet) throws SQLException {
        ArrayList<String[]> results = new ArrayList<String[]>();

        //getting userdata regarding files
        //iterating through the ResultSet of relevant files
        while(fileSet.next()){
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
            for(int i = 0, j = 1; i < 3; i++, j++){
                record[i] = rs.getString(j);
            }

            // add filename to record[]
            record[3] = fileSet.getString("filename");

            results.add(record);
        }
        return results;
    }


}
