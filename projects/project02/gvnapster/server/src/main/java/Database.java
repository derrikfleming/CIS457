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

    public void newClient(ArrayList<String> userData, ArrayList<String> fileList){
        int userID;

        writeUserData(userData);
        userID = getUserID(userData.get(0));
        writeFileList(userID, fileList);
    }

    private void writeUserData(ArrayList<String> userData){
        try {
            PreparedStatement ps =
                    conn.prepareStatement("INSERT INTO tblUsers VALUES(?,?,?,?,?);");

            for(int i = 0, j = 2; i < userData.size(); i++, j++) {
                if(i == 0)
                    ps.setInt(j, Integer.parseInt(userData.get(i)));

                ps.setString(j, userData.get(i));
            }

            ps.execute();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void writeFileList(int userID, ArrayList<String> fileList) {
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO tblFileList VALUES(?,?,?);");

            for (int i = 0; i < fileList.size(); i++) {
                ps.setInt(2, userID);
                ps.setString(3, fileList.get(i));
                ps.execute();
            }
        } catch (SQLException e) {
            System.out.println(e);
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
            String userID = fileSet.getString(2);

            // get the sharing user's info from tblUsers using the userID
            // the current record in tblFileList
            PreparedStatement ps =
                    conn.prepareStatement( "SELECT id, connType, address, port " +
                                                "FROM tblUsers " +
                                                "WHERE id = ?;");
            ps.setString(1, userID);
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
