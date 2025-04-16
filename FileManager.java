import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class FileManager {
    private Connection con;
    public FileManager(){          
        try {
            con = DriverManager.getConnection("jbdc:sqlite:Files.db");
            System.out.println("Connection to the database has been established");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
    public Connection getConnection(){
        return con;
    }
}
