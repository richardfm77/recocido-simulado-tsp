package mx.unam.heuristicas.heuristic;

import mx.unam.heuristicas.util.DoublePrecision;

import java.util.Objects;
import java.util.Random;
import java.util.random.RandomGenerator;

public class ThresholdAccepting<S>
        implements Heuristic<S> {

    private final ObjectiveFunction<S> objectiveFunction;
    private final Neighborhood<S> neighborhood;
    private final ThresholdAcceptingParameters parameters;

    public ThresholdAccepting(
            ObjectiveFunction<S> objectiveFunction,
            Neighborhood<S> neighborhood,
            ThresholdAcceptingParameters parameters
    ) {
        this.objectiveFunction =
                Objects.requireNonNull(objectiveFunction);

        this.neighborhood =
                Objects.requireNonNull(neighborhood);

        this.parameters =
                Objects.requireNonNull(parameters);
    }

    @Override
    public OptimizationResult<S> optimize(
            S initialSolution,
            long seed
    ) {

        Objects.requireNonNull(
                initialSolution,
                "La solución inicial no puede ser null"
        );

        RandomGenerator random = new Random(seed);

        S currentSolution = initialSolution;
        double currentCost =
                evaluate(currentSolution);

        S bestSolution = currentSolution;
        double bestCost = currentCost;

        double temperature =
                parameters.initialTemperature();

        double previousAverage = 0.0;

        long generatedNeighbors = 0;
        long acceptedNeighbors = 0;

        int temperatureLevels = 0;

        while (DoublePrecision.greaterThan(
                temperature,
                parameters.temperatureEpsilon()
        )) {

            double currentAverage =
                    Double.POSITIVE_INFINITY;

            while (DoublePrecision.lessThanOrEqual(
                    previousAverage,
                    currentAverage
            )) {

                currentAverage = previousAverage;

                BatchResult<S> batch =
                        calculateBatch(
                                temperature,
                                currentSolution,
                                currentCost,
                                random
                        );

                generatedNeighbors +=
                        batch.generatedNeighbors();

                acceptedNeighbors +=
                        batch.acceptedNeighbors();

                /*
                 * Si el lote no pudo completarse,
                 * dejamos de buscar equilibrio para
                 * esta temperatura.
                 */
                if (!batch.completed()) {
                    break;
                }

                previousAverage =
                        batch.averageCost();

                currentSolution =
                        batch.lastSolution();

                currentCost =
                        batch.lastCost();

                if (DoublePrecision.lessThan(
                        batch.bestCost(),
                        bestCost
                )) {
                    bestSolution =
                            batch.bestSolution();

                    bestCost =
                            batch.bestCost();
                }
            }

            temperature *=
                    parameters.coolingFactor();

            temperatureLevels++;
        }

        return new OptimizationResult<>(
                bestSolution,
                bestCost,
                currentSolution,
                currentCost,
                seed,
                generatedNeighbors,
                acceptedNeighbors,
                temperatureLevels
        );
    }

    private BatchResult<S> calculateBatch(
            double temperature,
            S initialSolution,
            double initialCost,
            RandomGenerator random
    ) {

        S currentSolution = initialSolution;
        double currentCost = initialCost;

        S bestSolution = initialSolution;
        double bestCost = initialCost;

        int accepted = 0;
        int attempts = 0;

        double accumulatedCost = 0.0;

        while (
                accepted < parameters.batchSize()
                &&
                attempts < parameters.maxAttemptsPerBatch()
        ) {

            attempts++;

            S neighbor =
                    neighborhood.generateNeighbor(
                            currentSolution,
                            random
                    );

            Objects.requireNonNull(
                    neighbor,
                    "Neighborhood devolvió null"
            );

            double neighborCost =
                    evaluate(neighbor);

            /*
             * f(s') <= f(s) + T
             */
            if (DoublePrecision.lessThanOrEqual(
                    neighborCost,
                    currentCost + temperature
            )) {

                currentSolution = neighbor;
                currentCost = neighborCost;

                accepted++;

                accumulatedCost +=
                        neighborCost;

                if (DoublePrecision.lessThan(
                        neighborCost,
                        bestCost
                )) {
                    bestSolution = neighbor;
                    bestCost = neighborCost;
                }
            }
        }

        boolean completed =
                accepted == parameters.batchSize();

        double averageCost =
                completed
                        ? accumulatedCost
                            / parameters.batchSize()
                        : Double.NaN;

        return new BatchResult<>(
                averageCost,
                currentSolution,
                currentCost,
                bestSolution,
                bestCost,
                attempts,
                accepted,
                completed
        );
    }

    private double evaluate(S solution) {

        double cost =
                objectiveFunction.evaluate(solution);

        if (!Double.isFinite(cost)) {
            throw new IllegalArgumentException(
                    "La función objetivo produjo "
                    + "un costo no finito: "
                    + cost
            );
        }

        if (cost < 0.0
                && !DoublePrecision.equals(cost, 0.0)) {

            throw new IllegalArgumentException(
                    "La función objetivo produjo "
                    + "un costo negativo: "
                    + cost
            );
        }

        return cost;
    }

    private record BatchResult<S>(
            double averageCost,
            S lastSolution,
            double lastCost,
            S bestSolution,
            double bestCost,
            int generatedNeighbors,
            int acceptedNeighbors,
            boolean completed
    ) {
    }
}