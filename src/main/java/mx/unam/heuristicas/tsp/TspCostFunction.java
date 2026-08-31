package mx.unam.heuristicas.tsp;

import mx.unam.heuristicas.heuristic.ObjectiveFunction;

import java.util.Objects;

public final class TspCostFunction
        implements ObjectiveFunction<TspSolution> {

    private final TspInstance instance;

    public TspCostFunction(TspInstance instance) {
        this.instance =
                Objects.requireNonNull(
                        instance,
                        "La instancia TSP no puede ser null"
                );
    }

    @Override
    public double evaluate(TspSolution solution) {

        Objects.requireNonNull(
                solution,
                "La solución no puede ser null"
        );

        double totalCost = 0.0;

        for (int i = 1; i < solution.size(); i++) {

            int previous =
                    solution.get(i - 1);

            int current =
                    solution.get(i);

            totalCost +=
                    instance.getWeight(
                            previous,
                            current
                    );
        }

        return totalCost /
                instance.getNormalizer();
    }
}