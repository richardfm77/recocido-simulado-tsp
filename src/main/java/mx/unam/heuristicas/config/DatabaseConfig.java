package mx.unam.heuristicas.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {

    private final Properties properties = new Properties();

    public DatabaseConfig(String resourceName) {

        try (InputStream input =
                     getClass()
                         .getClassLoader()
                         .getResourceAsStream(resourceName)) {

            if (input == null) {
                throw new IllegalArgumentException(
                        "No se encontró el archivo: " + resourceName
                );
            }

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Error leyendo configuración de base de datos",
                    e
            );
        }
    }

    public String getType() {
        return properties.getProperty("db.type");
    }

    public String getUrl() {
        return properties.getProperty("db.url");
    }

    public String getUser() {
        return properties.getProperty("db.user");
    }

    public String getPassword() {
        return properties.getProperty("db.password");
    }
}