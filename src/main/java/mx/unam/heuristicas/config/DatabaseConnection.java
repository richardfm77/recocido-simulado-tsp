package mx.unam.heuristicas.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private final DatabaseConfig config;

    public DatabaseConnection(DatabaseConfig config) {
        this.config = config;
    }

    public Connection getConnection() throws SQLException {

        if ("sqlite".equalsIgnoreCase(config.getType())) {
            return DriverManager.getConnection(
                    config.getUrl());
        }

        return DriverManager.getConnection(
                config.getUrl(),
                config.getUser(),
                config.getPassword());
    }
}