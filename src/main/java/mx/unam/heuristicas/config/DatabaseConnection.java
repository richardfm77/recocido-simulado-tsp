package mx.unam.heuristicas.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import mx.unam.heuristicas.exception.AppException;

public class DatabaseConnection {

    private final DatabaseConfig config;

    public DatabaseConnection(DatabaseConfig config) {
        this.config = config;
    }

    public Connection getConnection() throws AppException {

        try {
            if ("sqlite".equalsIgnoreCase(config.getType())) {
                return DriverManager.getConnection(
                        config.getUrl());
            }

            return DriverManager.getConnection(
                    config.getUrl(),
                    config.getUser(),
                    config.getPassword());
        } catch (SQLException e) {
            throw new AppException(
                    "Error al establecer la conexión con la base de datos",
                    e);
        }
    }
}