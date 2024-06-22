
import java.sql.*;

public class Server {
    private static final String URL = "jdbc:mysql://localhost:3306/chat_application";
    private static final String USER = "root";
    private static final String PASSWORD = null;

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}


