package mx.unam.heuristicas.tsp;

import mx.unam.heuristicas.heuristic.ObjectiveFunction;

import java.util.Objects;

public final class TspCostFunction
        implements ObjectiveFunction<TspSolution> {

    private final double[][] weights;
    private final double normalizer;

    public TspCostFunction(
            double[][] weights,
            double normalizer
    ) {
        Objects.requireNonNull(
                weights,
                "La matriz de pesos no puede ser null"
        );

        if (weights.length == 0) {
            throw new IllegalArgumentException(
                    "La matriz de pesos no puede estar vacía"
            );
        }

        if (normalizer <= 0.0 || !Double.isFinite(normalizer)) {
            throw new IllegalArgumentException(
                    "El normalizador debe ser positivo y finito"
            );
        }

        this.weights = weights;
        this.normalizer = normalizer;
    }

    @Override
    public double evaluate(TspSolution solution) {

        Objects.requireNonNull(
                solution,
                "La solución no puede ser null"
        );

        double totalCost = 0.0;

        for (int i = 1; i < solution.size(); i++) {

            int previousCity =
                    solution.get(i - 1);

            int currentCity =
                    solution.get(i);

            totalCost += weights[previousCity][currentCity];
        }

        return totalCost / normalizer;
    }
}