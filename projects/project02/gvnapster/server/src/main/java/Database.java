import java.sql.*;
import java.util.ArrayList;

public class Database {

    private Connection conn;
    //private boolean empty = true;

    public Database(Connection conn) {
        this.conn = conn;
        connect();
    }

    private void connect() {
        try {
            PreparedStatement ps =
                    conn.prepareStatement("PRAGMA foreign_keys = ON;");
            ps.execute();
            //testing
            System.out.println("Connected to Napster DB!");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Add new client info and file list to the database.
     * @param clientInfo Client info.
     * @param clientFileInfos List of files from client.
     */
    public void newClient(Info clientInfo, ArrayList<FileInfo> clientFileInfos) throws SQLException {
        int userID;
        String username = clientInfo.getUsername();
        if(!userExists(username)){
            writeUserData(clientInfo);
            userID = getUserID(username);
            // TODO: FIXME
            writeFileList(userID, clientFileInfos);
//            try {
//                userID = getUserID(username);
//                writeFileList(userID, clientFileInfos);
//            } catch (NumberFormatException e) {
//                e.printStackTrace();
//            }
        }
    }

    /**
     * Add new client info and file list to the database.
     * Wrapper to newClient(Info clientInfo, ArrayList<FileInfo> clientFileInfos)
     * @param clientFileInfos List of files from client, along with associated client info.
     */
    public void newClient(ArrayList<FileInfo> clientFileInfos) throws SQLException {
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
                    conn.prepareStatement("INSERT INTO tblUsers (username, address, port, connType)" +
                                                "VALUES(?,?,?,?);");
//            ps.setInt(0, null);
            ps.setString(1, clientInfo.getUsername());
            ps.setString(2, clientInfo.getAddress());
            ps.setInt(3, clientInfo.getPort());
            ps.setString(4, clientInfo.getConType());
            ps.execute();
        } catch (SQLException e) {
//            System.out.println(e);
            e.printStackTrace();
        }
    }

    private void writeFileList(int userID, ArrayList<FileInfo> clientFileInfos) {
        for (int i = 0; i < clientFileInfos.size(); i++) {
            System.out.println(clientFileInfos.get(i).getFilename());
            try {
                PreparedStatement ps =
                        conn.prepareStatement("INSERT INTO tblFileList (userID, filename)" +
                                                    "VALUES(?,?);");
                ps.setInt(1, userID);
                ps.setString(2, clientFileInfos.get(i).getFilename());
                // TODO: FIXME
                ps.execute();
            } catch (SQLException e) {
//                System.out.println(e);
                e.printStackTrace();
            }
        }
    }

    private int getUserID(String username){
//        int userID = Integer.parseInt(null);
        int userID = 0;

        try {
            PreparedStatement ps =
                    conn.prepareStatement( "SELECT * " +
                                                "FROM tblUsers " +
                                                "WHERE username = ?;");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            userID = rs.getInt("id");
        } catch (SQLException e) {
//            System.out.println(e);
            e.printStackTrace();
        }

        return userID;
    }

    public void clientDisconnect (Info clientInfo) {
        int userID = getUserID(clientInfo.getUsername());

        try {
            deleteUser(userID);
        } catch (SQLException e) {
//            System.out.println(e);
            e.printStackTrace();
        }

    }

    private void deleteUser (int userID) throws SQLException {
        PreparedStatement ps =
                conn.prepareStatement( "DELETE FROM tblUsers WHERE id = ?");
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
                                                "FROM tblFileList, tblUsers " +
                                                "WHERE tblUsers.id = tblFileList.userID " +
                                                "AND tblFileList.filename LIKE ?");
            ps.setString(1, "%" + searchTerm + "%");

            // records of relevant files
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int userID = rs.getInt("userID");
                String filename = rs.getString("filename");
                Info info = new Info();

                info.setUsername(rs.getString("username"));
                info.setAddress(rs.getString("address"));
                info.setPort(rs.getInt("port"));
                info.setConType(rs.getString("connType"));

                FileInfo file = new FileInfo(info, filename);

                System.out.println("********* Search Result **********");
                System.out.println("username -> " + file.getUsername());
                System.out.println("address  -> " + file.getAddress());
                System.out.println("port     -> " + file.getPort());
                System.out.println("connType -> " + file.getConType());
                System.out.println("filename -> " + file.getFilename());

                results.add(file);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return results;
    }
}
