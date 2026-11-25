package mg.itu.framework.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static  DatabaseConfig config;

    public static void configure(DatabaseConfig databaseConfig){
        config = databaseConfig;
    }

    public static Connection getConnection() throws SQLException{
        if(config == null){
            throw new SQLException("La base de donnee n est pas configuree.");
        }

        return DriverManager.getConnection(
            config.getUrl(),
            config.getUsername(),
            config.getPassword()
        );
    }
}
