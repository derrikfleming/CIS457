import java.sql.*;

public class Database {

    private Connection conn;
    //private boolean empty = true;

    public Database() throws ClassNotFoundException, SQLException {
        try {
            connect();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e);
        }
    }

    private void connect() throws ClassNotFoundException, SQLException {
        try {
            Class.forName("org.sqlite.JDBC");

            // path relative to module dir
            conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/napster.db");

            //testing
            System.out.println("Connected to Napster DB!");

            //testing
            selectAll();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e);
        }
    }

    //testing
    private void selectAll() {
        try {
            Statement statement = conn.createStatement();
            ResultSet set = statement.executeQuery("SELECT * FROM tblFileList;");



            while(set.next()) {
                System.out.println("id,\tconType,\taddress,\tfilename");
                System.out.print(set.getString("id"));
                System.out.print(", " + set.getString("conType"));
                System.out.print(", " + set.getString("address"));
                System.out.println(", " + set.getString("filename"));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void addFile (String conType, String address, String filename) {
        try {
            PreparedStatement prepStatement = conn.prepareStatement("INSERT INTO tblFileList values(?,?,?,?);");
            prepStatement.setString(2, conType);
            prepStatement.setString(3, address);
            prepStatement.setString(4, filename);
            prepStatement.execute();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}
