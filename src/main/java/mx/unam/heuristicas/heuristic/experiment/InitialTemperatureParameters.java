package mx.unam.heuristicas.heuristic.experiment;

public record InitialTemperatureParameters(
        double initialGuess,
        double targetAcceptance,
        double acceptanceEpsilon,
        int samples,
        int maxIterations
) {

    public InitialTemperatureParameters {

        if (initialGuess <= 0.0) {
            throw new IllegalArgumentException(
                    "La temperatura inicial de búsqueda debe ser positiva"
            );
        }

        if (targetAcceptance <= 0.0
                || targetAcceptance >= 1.0) {
            throw new IllegalArgumentException(
                    "El porcentaje objetivo debe estar entre 0 y 1"
            );
        }

        if (acceptanceEpsilon <= 0.0) {
            throw new IllegalArgumentException(
                    "El epsilon de aceptación debe ser positivo"
            );
        }

        if (samples <= 0) {
            throw new IllegalArgumentException(
                    "El número de muestras debe ser positivo"
            );
        }

        if (maxIterations <= 0) {
            throw new IllegalArgumentException(
                    "El máximo de iteraciones debe ser positivo"
            );
        }
    }
}