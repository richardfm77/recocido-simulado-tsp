package mx.unam.heuristicas.heuristic.experiment;

public record ExperimentParameters(
        double coolingFactor,
        double temperatureEpsilon,
        int batchSize,
        int maxAttemptFactor,
        double initialTemperatureGuess,
        double targetAcceptance,
        double acceptanceEpsilon,
        int temperatureSamples,
        int temperatureMaxIterations
) {

    public ExperimentParameters {

        if (coolingFactor <= 0.0 || coolingFactor >= 1.0) {
            throw new IllegalArgumentException(
                    "El factor de enfriamiento debe estar entre 0 y 1"
            );
        }

        if (temperatureEpsilon <= 0.0) {
            throw new IllegalArgumentException(
                    "El epsilon de temperatura debe ser positivo"
            );
        }

        if (batchSize <= 0) {
            throw new IllegalArgumentException(
                    "El tamaño del lote debe ser positivo"
            );
        }

        if (maxAttemptFactor <= 0) {
            throw new IllegalArgumentException(
                    "El factor máximo de intentos debe ser positivo"
            );
        }

        if (initialTemperatureGuess <= 0.0) {
            throw new IllegalArgumentException(
                    "La temperatura inicial de búsqueda debe ser positiva"
            );
        }

        if (targetAcceptance <= 0.0
                || targetAcceptance >= 1.0) {

            throw new IllegalArgumentException(
                    "El porcentaje objetivo de aceptación debe estar entre 0 y 1"
            );
        }

        if (acceptanceEpsilon <= 0.0) {
            throw new IllegalArgumentException(
                    "El epsilon de aceptación debe ser positivo"
            );
        }

        if (temperatureSamples <= 0) {
            throw new IllegalArgumentException(
                    "El número de muestras debe ser positivo"
            );
        }

        if (temperatureMaxIterations <= 0) {
            throw new IllegalArgumentException(
                    "El máximo de iteraciones debe ser positivo"
            );
        }
    }

    public int maxAttemptsPerBatch() {

        return Math.multiplyExact(
                batchSize,
                maxAttemptFactor
        );
    }
}