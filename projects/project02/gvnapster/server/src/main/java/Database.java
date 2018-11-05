import java.sql.*;

public class Database {

    private Connection con;
    //private boolean empty = true;

    public Database() throws ClassNotFoundException, SQLException {
        try {
            connect();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e);
        }
    }

    private void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:napster.db");
    }

    public void addFile (String conType, String address, String filename) {
        try {
            PreparedStatement prepStatement = con.prepareStatement("INSERT INTO tblFileList values(?,?,?,?);");
            prepStatement.setString(2, conType);
            prepStatement.setString(3, address);
            prepStatement.setString(4, filename);
            prepStatement.execute();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}
