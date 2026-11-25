package mg.itu.framework.database;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {
    
    public static void configure(DatabaseConfig config){
        DatabaseManager.configure(config);
    }

    public static Connection getConnection() throws SQLException{
        return DatabaseManager.getConnection();
    }
}
