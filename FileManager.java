import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class FileManager {
    private Connection con;
    public FileManager(){          
        try {
            con = DriverManager.getConnection("jdbc:sqlite:Files.db");
            System.out.println("Connection to the database has been established");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
    public Connection getConnection(){
        return con;
    }
    public void closeConnection() {
        try {
            if (con!= null) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
