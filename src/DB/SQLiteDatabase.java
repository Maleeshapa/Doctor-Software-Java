package DB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteDatabase {
    public static Connection connection;
    private static int connectionCount = 0;
    private static final String DATABASE_URL = "jdbc:sqlite:new_database.db";
    
        static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
        }
    }
    
    public static synchronized void connect() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DATABASE_URL);
                
                createTables();
                
                System.out.println("Database connected successfully");
            }
            connectionCount++;
        } catch (SQLException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }
    
    private static void createTables() {
        try (Statement stmt = connection.createStatement()) {
            String createHistoryTableSQL = "CREATE TABLE IF NOT EXISTS history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "dateTime DATETIME NULL, " +
                "name TEXT NULL, " +
                "age TEXT NULL, " +
                "phone TEXT NULL, " +
                "address TEXT NULL, " +
                "illness TEXT NULL, " +
                "medicine TEXT NULL, " +
                "note TEXT NULL, " +
                "fee INTEGER NULL" +
            ")";
            
            stmt.execute(createHistoryTableSQL);
            System.out.println("Tables created or already exist.");
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }
    
    public static synchronized void disconnect() {
        connectionCount--;
        if (connectionCount <= 0) {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    System.out.println("Database connection closed.");
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
            connectionCount = 0;
        }
    }
    
    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}