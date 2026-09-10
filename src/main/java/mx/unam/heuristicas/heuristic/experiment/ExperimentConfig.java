package mx.unam.heuristicas.heuristic.experiment;

import mx.unam.heuristicas.exception.AppException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Properties;

public final class ExperimentConfig {

    private final int generations;
    private final int candidatesPerGeneration;

    private final long[] seeds;

    private final double coolingFactorMin;
    private final double coolingFactorMax;

    private final double temperatureEpsilonMin;
    private final double temperatureEpsilonMax;

    private final int batchSizeMin;
    private final int batchSizeMax;

    private final int maxAttemptFactorMin;
    private final int maxAttemptFactorMax;

    private final double initialTemperatureGuess;

    private final double targetAcceptanceMin;
    private final double targetAcceptanceMax;

    private final double acceptanceEpsilon;
    private final int temperatureSamples;
    private final int temperatureMaxIterations;

    private final double explorationProbability;
    private final double variationFactor;

    private final long parameterSeed;

    private ExperimentConfig(Properties properties) {

        generations =
                getPositiveInt(
                        properties,
                        "experiment.generations"
                );

        candidatesPerGeneration =
                getPositiveInt(
                        properties,
                        "experiment.candidates-per-generation"
                );

        seeds =
                getLongArray(
                        properties,
                        "experiment.seeds"
                );

        coolingFactorMin =
                getDouble(
                        properties,
                        "experiment.cooling-factor.min"
                );

        coolingFactorMax =
                getDouble(
                        properties,
                        "experiment.cooling-factor.max"
                );

        temperatureEpsilonMin =
                getPositiveDouble(
                        properties,
                        "experiment.temperature-epsilon.min"
                );

        temperatureEpsilonMax =
                getPositiveDouble(
                        properties,
                        "experiment.temperature-epsilon.max"
                );

        batchSizeMin =
                getPositiveInt(
                        properties,
                        "experiment.batch-size.min"
                );

        batchSizeMax =
                getPositiveInt(
                        properties,
                        "experiment.batch-size.max"
                );

        maxAttemptFactorMin =
                getPositiveInt(
                        properties,
                        "experiment.max-attempt-factor.min"
                );

        maxAttemptFactorMax =
                getPositiveInt(
                        properties,
                        "experiment.max-attempt-factor.max"
                );

        initialTemperatureGuess =
                getPositiveDouble(
                        properties,
                        "experiment.initial-temperature.guess"
                );

        targetAcceptanceMin =
                getDouble(
                        properties,
                        "experiment.target-acceptance.min"
                );

        targetAcceptanceMax =
                getDouble(
                        properties,
                        "experiment.target-acceptance.max"
                );

        acceptanceEpsilon =
                getPositiveDouble(
                        properties,
                        "experiment.acceptance-epsilon"
                );

        temperatureSamples =
                getPositiveInt(
                        properties,
                        "experiment.temperature-samples"
                );

        temperatureMaxIterations =
                getPositiveInt(
                        properties,
                        "experiment.temperature-max-iterations"
                );

        explorationProbability =
                getDouble(
                        properties,
                        "experiment.exploration-probability"
                );

        variationFactor =
                getPositiveDouble(
                        properties,
                        "experiment.variation-factor"
                );

        parameterSeed =
                getLong(
                        properties,
                        "experiment.parameter-seed"
                );

        validate();
    }

    public static ExperimentConfig load(Path resource) {

        if (resource == null) {
            throw new IllegalArgumentException(
                    "El archivo de configuración no puede ser null o vacío"
            );
        }

        Properties properties =
                new Properties();

        try (InputStream input = Files.newInputStream(resource)) {

            if (input == null) {
                throw new AppException(
                        "No se encontró el archivo de configuración: "
                                + resource
                );
            }

            properties.load(input);

        } catch (IOException e) {

            throw new AppException(
                    "No fue posible leer el archivo de configuración: "
                            + resource,
                    e
            );
        }

        return new ExperimentConfig(
                properties
        );
    }

    static ExperimentConfig from(
            Properties properties
    ) {

        return new ExperimentConfig(
                properties
        );
    }

    private void validate() {

        if (seeds.length == 0) {
            throw new AppException(
                    "Debe existir al menos una semilla experimental"
            );
        }

        validateRange(
                coolingFactorMin,
                coolingFactorMax,
                "coolingFactor"
        );

        if (coolingFactorMin <= 0.0
                || coolingFactorMax >= 1.0) {

            throw new AppException(
                    "coolingFactor debe permanecer entre 0 y 1"
            );
        }

        validateRange(
                temperatureEpsilonMin,
                temperatureEpsilonMax,
                "temperatureEpsilon"
        );

        validateRange(
                batchSizeMin,
                batchSizeMax,
                "batchSize"
        );

        validateRange(
                maxAttemptFactorMin,
                maxAttemptFactorMax,
                "maxAttemptFactor"
        );

        validateRange(
                targetAcceptanceMin,
                targetAcceptanceMax,
                "targetAcceptance"
        );

        if (targetAcceptanceMin <= 0.0
                || targetAcceptanceMax >= 1.0) {

            throw new AppException(
                    "targetAcceptance debe permanecer entre 0 y 1"
            );
        }

        if (explorationProbability < 0.0
                || explorationProbability > 1.0) {

            throw new AppException(
                    "explorationProbability debe estar entre 0 y 1"
            );
        }
    }

    private static void validateRange(
            double min,
            double max,
            String name
    ) {

        if (min > max) {
            throw new AppException(
                    "Rango inválido para "
                            + name
                            + ": min > max"
            );
        }
    }

    private static void validateRange(
            int min,
            int max,
            String name
    ) {

        if (min > max) {
            throw new AppException(
                    "Rango inválido para "
                            + name
                            + ": min > max"
            );
        }
    }

    private static String getRequired(
            Properties properties,
            String key
    ) {

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

    private static int getPositiveInt(
            Properties properties,
            String key
    ) {

        int value =
                getInt(
                        properties,
                        key
                );

        if (value <= 0) {
            throw new AppException(
                    key + " debe ser positivo"
            );
        }

        return value;
    }

    private static int getInt(
            Properties properties,
            String key
    ) {

        try {
            return Integer.parseInt(
                    getRequired(
                            properties,
                            key
                    )
            );

        } catch (NumberFormatException e) {

            throw new AppException(
                    "Valor entero inválido para "
                            + key,
                    e
            );
        }
    }

    private static long getLong(
            Properties properties,
            String key
    ) {

        try {
            return Long.parseLong(
                    getRequired(
                            properties,
                            key
                    )
            );

        } catch (NumberFormatException e) {

            throw new AppException(
                    "Valor long inválido para "
                            + key,
                    e
            );
        }
    }

    private static double getPositiveDouble(
            Properties properties,
            String key
    ) {

        double value =
                getDouble(
                        properties,
                        key
                );

        if (value <= 0.0) {
            throw new AppException(
                    key + " debe ser positivo"
            );
        }

        return value;
    }

    private static double getDouble(
            Properties properties,
            String key
    ) {

        try {
            return Double.parseDouble(
                    getRequired(
                            properties,
                            key
                    )
            );

        } catch (NumberFormatException e) {

            throw new AppException(
                    "Valor double inválido para "
                            + key,
                    e
            );
        }
    }

    private static long[] getLongArray(
            Properties properties,
            String key
    ) {

        String raw =
                getRequired(
                        properties,
                        key
                );

        try {

            return Arrays.stream(
                            raw.split(",")
                    )
                    .map(String::trim)
                    .filter(value ->
                            !value.isEmpty()
                    )
                    .mapToLong(
                            Long::parseLong
                    )
                    .toArray();

        } catch (NumberFormatException e) {

            throw new AppException(
                    "Lista de semillas inválida para "
                            + key,
                    e
            );
        }
    }

    public int generations() {
        return generations;
    }

    public int candidatesPerGeneration() {
        return candidatesPerGeneration;
    }

    public long[] seeds() {
        return seeds.clone();
    }

    public double coolingFactorMin() {
        return coolingFactorMin;
    }

    public double coolingFactorMax() {
        return coolingFactorMax;
    }

    public double temperatureEpsilonMin() {
        return temperatureEpsilonMin;
    }

    public double temperatureEpsilonMax() {
        return temperatureEpsilonMax;
    }

    public int batchSizeMin() {
        return batchSizeMin;
    }

    public int batchSizeMax() {
        return batchSizeMax;
    }

    public int maxAttemptFactorMin() {
        return maxAttemptFactorMin;
    }

    public int maxAttemptFactorMax() {
        return maxAttemptFactorMax;
    }

    public double initialTemperatureGuess() {
        return initialTemperatureGuess;
    }

    public double targetAcceptanceMin() {
        return targetAcceptanceMin;
    }

    public double targetAcceptanceMax() {
        return targetAcceptanceMax;
    }

    public double acceptanceEpsilon() {
        return acceptanceEpsilon;
    }

    public int temperatureSamples() {
        return temperatureSamples;
    }

    public int temperatureMaxIterations() {
        return temperatureMaxIterations;
    }

    public double explorationProbability() {
        return explorationProbability;
    }

    public double variationFactor() {
        return variationFactor;
    }

    public long parameterSeed() {
        return parameterSeed;
    }
}