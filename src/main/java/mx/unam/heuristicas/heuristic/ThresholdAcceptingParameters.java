package mx.unam.heuristicas.heuristic;

public record ThresholdAcceptingParameters(
        double initialTemperature,
        double temperatureEpsilon,
        double coolingFactor,
        int batchSize,
        int maxAttemptsPerBatch
) {

    public ThresholdAcceptingParameters {

        if (initialTemperature < 0.0) {
            throw new IllegalArgumentException(
                    "La temperatura inicial no puede ser negativa"
            );
        }

        if (temperatureEpsilon <= 0.0) {
            throw new IllegalArgumentException(
                    "El epsilon de temperatura debe ser positivo"
            );
        }

        if (coolingFactor <= 0.0
                || coolingFactor >= 1.0) {

            throw new IllegalArgumentException(
                    "El factor de enfriamiento debe estar entre 0 y 1"
            );
        }

        if (batchSize <= 0) {
            throw new IllegalArgumentException(
                    "El tamaño del lote debe ser positivo"
            );
        }

        if (maxAttemptsPerBatch < batchSize) {
            throw new IllegalArgumentException(
                    "El máximo de intentos debe ser al menos el tamaño del lote"
            );
        }
    }
}