package mx.unam.heuristicas.heuristic;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ThresholdAcceptingTest {

    private static final double EPSILON = 1e-7;

    /**
     * Problema artificial extremadamente sencillo:
     * la solución ES el costo.
     */
    private final ObjectiveFunction<Double> objectiveFunction = value -> value;

    @Test
    void shouldImproveSolutionWhenBetterNeighborsExist() {
        for (int i = 0; i < 20; i++) {
            Neighborhood<Double> neighborhood = (solution, random) -> {

                double movement = random.nextDouble(0.1, 1.0);

                if (solution <= movement) {
                    return solution + movement;
                }

                if (random.nextBoolean()) {
                    return solution + movement;
                }

                return solution - movement;
            };

            ThresholdAcceptingParameters parameters = new ThresholdAcceptingParameters(
                    1.0,
                    0.1,
                    0.5,
                    3,
                    100);

            ThresholdAccepting<Double> heuristic = new ThresholdAccepting<>(
                    objectiveFunction,
                    neighborhood,
                    parameters);

            OptimizationResult<Double> result = heuristic.optimize(
                    10.0,
                    123L);

            assertTrue(result.bestCost() < 10.0);

            assertTrue(result.bestSolution() < 10.0);
        }
    }

    @Test
    void shouldAcceptWorseNeighborInsideThreshold() {
        for (int i = 0; i < 20; i++) {
            /*
             * El vecino siempre cuesta exactamente 0.5 más.
             *
             * Con T = 1.0:
             *
             * f(s') <= f(s) + T
             *
             * s + 0.5 <= s + 1.0
             *
             * por lo tanto debe aceptarse.
             */
            Neighborhood<Double> neighborhood = (solution, random) -> solution + 0.5;

            ThresholdAcceptingParameters parameters = new ThresholdAcceptingParameters(
                    1.0,
                    0.9,
                    0.5,
                    1,
                    10);

            ThresholdAccepting<Double> heuristic = new ThresholdAccepting<>(
                    objectiveFunction,
                    neighborhood,
                    parameters);

            OptimizationResult<Double> result = heuristic.optimize(
                    10.0,
                    1L);

            assertTrue(result.acceptedNeighbors() > 0);
        }
    }

    @Test
    void shouldRejectWorseNeighborOutsideThreshold() {
        for (int i = 0; i < 20; i++) {
            /*
             * T = 1
             *
             * vecino = actual + 2
             *
             * actual + 2 <= actual + 1
             *
             * es falso.
             */
            Neighborhood<Double> neighborhood = (solution, random) -> solution + 2.0;

            ThresholdAcceptingParameters parameters = new ThresholdAcceptingParameters(
                    1.0,
                    0.9,
                    0.5,
                    1,
                    5);

            ThresholdAccepting<Double> heuristic = new ThresholdAccepting<>(
                    objectiveFunction,
                    neighborhood,
                    parameters);

            OptimizationResult<Double> result = heuristic.optimize(
                    10.0,
                    1L);

            assertEquals(
                    0,
                    result.acceptedNeighbors());

            assertEquals(
                    10.0,
                    result.finalCost(),
                    EPSILON);

            assertEquals(
                    10.0,
                    result.bestCost(),
                    EPSILON);
        }
    }

    @Test
    void shouldKeepBestSolutionEvenIfLaterSolutionsAreWorse() {
        for (int i = 0; i < 20; i++) {
            /*
             * Secuencia artificial:
             *
             * 10 -> 5 -> 6
             *
             * La solución final puede ser 6,
             * pero la mejor debe seguir siendo 5.
             */
            List<Double> neighbors = new ArrayList<>(
                    List.of(
                            5.0,
                            6.0));

            Neighborhood<Double> neighborhood = (solution, random) -> {

                if (neighbors.isEmpty()) {
                    return solution + 100.0;
                }

                return neighbors.remove(0);
            };

            ThresholdAcceptingParameters parameters = new ThresholdAcceptingParameters(
                    10.0,
                    9.0,
                    0.5,
                    2,
                    2);

            ThresholdAccepting<Double> heuristic = new ThresholdAccepting<>(
                    objectiveFunction,
                    neighborhood,
                    parameters);

            OptimizationResult<Double> result = heuristic.optimize(
                    10.0,
                    42L);

            assertEquals(
                    5.0,
                    result.bestCost(),
                    EPSILON);

            assertEquals(
                    5.0,
                    result.bestSolution(),
                    EPSILON);
        }
    }

    @Test
    void shouldProduceSameResultWithSameSeed() {
        for (int i = 0; i < 20; i++) {
            Neighborhood<Double> neighborhood = (solution, random) -> {

                double movement = random.nextDouble(
                        -1.0,
                        1.0);

                return Math.max(
                        0.0,
                        solution + movement);
            };

            ThresholdAcceptingParameters parameters = new ThresholdAcceptingParameters(
                    1.0,
                    0.01,
                    0.8,
                    5,
                    100);

            ThresholdAccepting<Double> heuristic = new ThresholdAccepting<>(
                    objectiveFunction,
                    neighborhood,
                    parameters);

            OptimizationResult<Double> result1 = heuristic.optimize(
                    10.0,
                    987654321L);

            OptimizationResult<Double> result2 = heuristic.optimize(
                    10.0,
                    987654321L);

            assertEquals(
                    result1.bestCost(),
                    result2.bestCost(),
                    EPSILON);

            assertEquals(
                    result1.finalCost(),
                    result2.finalCost(),
                    EPSILON);

            assertEquals(
                    result1.generatedNeighbors(),
                    result2.generatedNeighbors());

            assertEquals(
                    result1.acceptedNeighbors(),
                    result2.acceptedNeighbors());

            assertEquals(
                    result1.temperatureLevels(),
                    result2.temperatureLevels());
        }
    }

    @Test
    void shouldStopWhenBatchCannotBeCompleted() {
        for (int i = 0; i < 20; i++) {
            /*
             * Ningún vecino puede aceptarse.
             *
             * El límite de intentos debe impedir
             * un ciclo infinito.
             */
            Neighborhood<Double> neighborhood = (solution, random) -> solution + 1000.0;

            ThresholdAcceptingParameters parameters = new ThresholdAcceptingParameters(
                    1.0,
                    0.9,
                    0.5,
                    10,
                    20);

            ThresholdAccepting<Double> heuristic = new ThresholdAccepting<>(
                    objectiveFunction,
                    neighborhood,
                    parameters);

            OptimizationResult<Double> result = heuristic.optimize(
                    10.0,
                    123L);

            assertEquals(
                    0,
                    result.acceptedNeighbors());

            assertTrue(
                    result.generatedNeighbors() > 0);

            assertEquals(
                    10.0,
                    result.bestCost(),
                    EPSILON);
        }
    }

    @Test
    void shouldAcceptValueWithinDoublePrecisionTolerance() {
        for (int i = 0; i < 20; i++) {
            /*
             * Matemáticamente:
             *
             * límite = 11.0
             *
             * vecino = 11.00000005
             *
             * diferencia = 5e-8
             *
             * Como nuestra tolerancia es 1e-7,
             * se considera aceptable.
             */
            Neighborhood<Double> neighborhood = (solution, random) -> solution
                    + 1.0
                    + 0.00000005;

            ThresholdAcceptingParameters parameters = new ThresholdAcceptingParameters(
                    1.0,
                    0.9,
                    0.5,
                    1,
                    1);

            ThresholdAccepting<Double> heuristic = new ThresholdAccepting<>(
                    objectiveFunction,
                    neighborhood,
                    parameters);

            OptimizationResult<Double> result = heuristic.optimize(
                    10.0,
                    123L);

            assertEquals(
                    1,
                    result.acceptedNeighbors());
        }
    }
}