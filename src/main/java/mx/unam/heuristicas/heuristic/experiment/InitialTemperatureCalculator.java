package mx.unam.heuristicas.heuristic.experiment;

import mx.unam.heuristicas.exception.AppException;
import mx.unam.heuristicas.heuristic.Neighborhood;
import mx.unam.heuristicas.heuristic.ObjectiveFunction;

import java.util.Objects;
import java.util.Random;
import java.util.random.RandomGenerator;

public final class InitialTemperatureCalculator<S> {

    private final ObjectiveFunction<S> objectiveFunction;
    private final Neighborhood<S> neighborhood;
    private final InitialTemperatureParameters parameters;

    public InitialTemperatureCalculator(
            ObjectiveFunction<S> objectiveFunction,
            Neighborhood<S> neighborhood,
            InitialTemperatureParameters parameters) {
        this.objectiveFunction = Objects.requireNonNull(
                objectiveFunction,
                "La función objetivo no puede ser null");

        this.neighborhood = Objects.requireNonNull(
                neighborhood,
                "La vecindad no puede ser null");

        this.parameters = Objects.requireNonNull(
                parameters,
                "Los parámetros no pueden ser null");
    }

    public double calculate(
            S initialSolution,
            long seed) {

        Objects.requireNonNull(
                initialSolution,
                "La solución inicial no puede ser null");

        RandomGenerator random = new Random(seed);

        double temperature = parameters.initialGuess();

        double acceptedPercentage = calculateAcceptedPercentage(
                initialSolution,
                temperature,
                random);

        if (isTargetReached(
                acceptedPercentage)) {
            return temperature;
        }

        double lowerTemperature;
        double upperTemperature;

        if (acceptedPercentage < parameters.targetAcceptance()) {

            /*
             * Pseudocódigo:
             *
             * while p < P
             * T = 2T
             */
            int iterations = 0;

            while (acceptedPercentage < parameters.targetAcceptance()) {

                ensureIterationLimit(++iterations);

                temperature *= 2.0;

                ensureFiniteTemperature(
                        temperature);

                acceptedPercentage = calculateAcceptedPercentage(
                        initialSolution,
                        temperature,
                        random);
            }

            lowerTemperature = temperature / 2.0;

            upperTemperature = temperature;

        } else {

            /*
             * Pseudocódigo:
             *
             * while p > P
             * T = T/2
             */
            int iterations = 0;

            while (acceptedPercentage > parameters.targetAcceptance()) {

                ensureIterationLimit(++iterations);

                temperature /= 2.0;

                acceptedPercentage = calculateAcceptedPercentage(
                        initialSolution,
                        temperature,
                        random);
            }

            lowerTemperature = temperature;

            upperTemperature = temperature * 2.0;
        }

        return binarySearch(
                initialSolution,
                lowerTemperature,
                upperTemperature,
                random,
                0);
    }

    double calculateAcceptedPercentage(
            S initialSolution,
            double temperature,
            RandomGenerator random) {

        S currentSolution = initialSolution;

        double currentCost = evaluate(
                currentSolution);

        int accepted = 0;

        for (int i = 0; i < parameters.samples(); i++) {

            S neighbor = neighborhood.generateNeighbor(
                    currentSolution,
                    random);

            Objects.requireNonNull(
                    neighbor,
                    "Neighborhood devolvió null");

            double neighborCost = evaluate(
                    neighbor);

            /*
             * f(s') <= f(s) + T
             */
            if (neighborCost <= currentCost + temperature) {

                accepted++;

                currentSolution = neighbor;

                currentCost = neighborCost;
            }
        }

        return (double) accepted
                / parameters.samples();
    }

    private double binarySearch(
            S initialSolution,
            double lowerTemperature,
            double upperTemperature,
            RandomGenerator random,
            int iteration) {

        if (iteration >= parameters.maxIterations()) {
            throw new AppException(
                    "No fue posible calcular una temperatura inicial "
                            + "dentro del máximo de iteraciones");
        }

        double middleTemperature = (lowerTemperature + upperTemperature) / 2.0;

        /*
         * Procedimiento 5:
         *
         * if T2 - T1 < epsilonP
         * return Tm
         */
        if (upperTemperature - lowerTemperature < parameters.acceptanceEpsilon()) {

            return middleTemperature;
        }

        double acceptedPercentage = calculateAcceptedPercentage(
                initialSolution,
                middleTemperature,
                random);

        /*
         * if |P - p| < epsilonP
         * return Tm
         */
        if (isTargetReached(acceptedPercentage)) {
            return middleTemperature;
        }

        /*
         * if p > P
         * BUSQUEDABINARIA(s, T1, Tm, P)
         */
        if (acceptedPercentage > parameters.targetAcceptance()) {

            return binarySearch(
                    initialSolution,
                    lowerTemperature,
                    middleTemperature,
                    random,
                    iteration + 1);
        }

        /*
         * else
         * BUSQUEDABINARIA(s, Tm, T2, P)
         */
        return binarySearch(
                initialSolution,
                middleTemperature,
                upperTemperature,
                random,
                iteration + 1);
    }

    private boolean isTargetReached(
            double acceptedPercentage) {

        return Math.abs(
                parameters.targetAcceptance()
                        - acceptedPercentage) <= parameters.acceptanceEpsilon();
    }

    private void ensureIterationLimit(
            int iterations) {

        if (iterations > parameters.maxIterations()) {

            throw new AppException(
                    "No fue posible encontrar un intervalo "
                            + "para la temperatura inicial");
        }
    }

    private static void ensureFiniteTemperature(
            double temperature) {

        if (!Double.isFinite(temperature)) {
            throw new AppException(
                    "La búsqueda produjo una temperatura no finita");
        }
    }

    private double evaluate(S solution) {

        double cost = objectiveFunction.evaluate(
                solution);

        if (!Double.isFinite(cost)) {
            throw new IllegalArgumentException(
                    "La función objetivo produjo "
                            + "un costo no finito: "
                            + cost);
        }

        if (cost < 0.0) {
            throw new IllegalArgumentException(
                    "La función objetivo produjo "
                            + "un costo negativo: "
                            + cost);
        }

        return cost;
    }
}