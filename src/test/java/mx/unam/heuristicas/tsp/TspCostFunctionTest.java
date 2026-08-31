package mx.unam.heuristicas.tsp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TspCostFunctionTest {

    private static final double EPSILON = 1e-7;

    @Test
    void shouldCalculateCostOfSolution() {

        double[][] weights = {
                {0.0, 10.0, 30.0, 40.0},
                {10.0, 0.0, 20.0, 50.0},
                {30.0, 20.0, 0.0, 15.0},
                {40.0, 50.0, 15.0, 0.0}
        };

        TspCostFunction costFunction =
                new TspCostFunction(
                        weights,
                        100.0
                );

        TspSolution solution =
                new TspSolution(
                        new int[]{0, 1, 2, 3}
                );

        double result =
                costFunction.evaluate(solution);

        assertEquals(
                0.45,
                result,
                EPSILON
        );
    }
}