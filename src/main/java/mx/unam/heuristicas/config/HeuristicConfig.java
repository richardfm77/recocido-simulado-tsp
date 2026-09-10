package mx.unam.heuristicas.config;

import mx.unam.heuristicas.exception.AppException;
import mx.unam.heuristicas.heuristic.ThresholdAcceptingParameters;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class HeuristicConfig {

    private final Properties properties =
            new Properties();

    public HeuristicConfig(Path resourceName) {

        try (
                InputStream input = Files.newInputStream(resourceName);
                        
        ) {

            if (input == null) {
                throw new AppException(
                        "No se encontró el archivo de configuración: "
                        + resourceName
                );
            }

            properties.load(input);

        } catch (IOException e) {
            throw new AppException(
                    "No se pudo leer el archivo de configuración: "
                    + resourceName,
                    e
            );
        }
    }

    public ThresholdAcceptingParameters getParameters() {

        return new ThresholdAcceptingParameters(
                getDouble(
                        "heuristic.initial-temperature"
                ),
                getDouble(
                        "heuristic.temperature-epsilon"
                ),
                getDouble(
                        "heuristic.cooling-factor"
                ),
                getInt(
                        "heuristic.batch-size"
                ),
                getInt(
                        "heuristic.max-attempts-per-batch"
                )
        );
    }

    public long getSeed() {
        return getLong(
                "heuristic.seed"
        );
    }

    private double getDouble(String key) {

        String value =
                getRequiredProperty(key);

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new AppException(
                    "Valor inválido para "
                    + key
                    + ": "
                    + value,
                    e
            );
        }
    }

    private int getInt(String key) {

        String value =
                getRequiredProperty(key);

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new AppException(
                    "Valor inválido para "
                    + key
                    + ": "
                    + value,
                    e
            );
        }
    }

    private long getLong(String key) {

        String value =
                getRequiredProperty(key);

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new AppException(
                    "Valor inválido para "
                    + key
                    + ": "
                    + value,
                    e
            );
        }
    }

    private String getRequiredProperty(String key) {

        String value =
                properties.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new AppException(
                    "Falta la propiedad requerida: "
                    + key
            );
        }

        return value.trim();
    }
}