package mx.unam.heuristicas.heuristic.experiment;

import java.util.Objects;

public record ExperimentResult<S>(
        int generation,
        long baseSeed,
        long temperatureSeed,
        long executionSeed,

        ExperimentParameters parameters,
        double initialTemperature,

        double initialCost,
        double bestCost,
        double finalCost,

        S bestSolution,
        S finalSolution,

        long generatedNeighbors,
        long acceptedNeighbors,
        int temperatureLevels,

        long elapsedNanos
) {

    public ExperimentResult {

        if (generation < 0) {
            throw new IllegalArgumentException(
                    "La generación no puede ser negativa"
            );
        }

        Objects.requireNonNull(
                parameters,
                "Los parámetros experimentales no pueden ser null"
        );

        Objects.requireNonNull(
                bestSolution,
                "La mejor solución no puede ser null"
        );

        Objects.requireNonNull(
                finalSolution,
                "La solución final no puede ser null"
        );

        if (!Double.isFinite(initialTemperature)
                || initialTemperature <= 0.0) {

            throw new IllegalArgumentException(
                    "La temperatura inicial debe ser positiva y finita"
            );
        }

        validateCost(
                initialCost,
                "costo inicial"
        );

        validateCost(
                bestCost,
                "mejor costo"
        );

        validateCost(
                finalCost,
                "costo final"
        );

        if (generatedNeighbors < 0) {
            throw new IllegalArgumentException(
                    "El número de vecinos generados no puede ser negativo"
            );
        }

        if (acceptedNeighbors < 0) {
            throw new IllegalArgumentException(
                    "El número de vecinos aceptados no puede ser negativo"
            );
        }

        if (acceptedNeighbors > generatedNeighbors) {
            throw new IllegalArgumentException(
                    "Los vecinos aceptados no pueden superar a los generados"
            );
        }

        if (temperatureLevels < 0) {
            throw new IllegalArgumentException(
                    "Los niveles de temperatura no pueden ser negativos"
            );
        }

        if (elapsedNanos < 0) {
            throw new IllegalArgumentException(
                    "El tiempo de ejecución no puede ser negativo"
            );
        }
    }

    private static void validateCost(
            double cost,
            String name
    ) {

        if (!Double.isFinite(cost) || cost < 0.0) {
            throw new IllegalArgumentException(
                    "El " + name
                            + " debe ser finito y no negativo"
            );
        }
    }
}
