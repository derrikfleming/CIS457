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
            PreparedStatement ps = conn.prepareStatement("INSERT INTO tblUsers VALUES(?,?,?,?,?);");

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
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM tblUsers WHERE username = ?;");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            userID = rs.getInt("id");

        } catch (SQLException e) {
            System.out.println(e);
        }

        return userID;
    }
}
