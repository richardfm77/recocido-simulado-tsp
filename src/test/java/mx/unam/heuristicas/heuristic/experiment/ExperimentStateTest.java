package mx.unam.heuristicas.heuristic.experiment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExperimentStateTest {

    private static final ExperimentParameters PARAMETERS =
            new ExperimentParameters(
                    0.95,
                    1e-5,
                    100,
                    10,
                    1.0,
                    0.90,
                    0.01,
                    1000,
                    100
            );

    @Test
    void firstResultShouldBecomeBestOverallResult() {

        ExperimentState<String> state =
                new ExperimentState<>();

        ExperimentResult<String> result =
                createResult(
                        0.50,
                        "solution-1"
                );

        boolean improved =
                state.register(result);

        assertTrue(improved);

        assertSame(
                result,
                state.bestOverallResult()
        );

        assertEquals(
                1,
                state.completedRuns()
        );
    }

    @Test
    void betterResultShouldReplaceBestOverallResult() {

        ExperimentState<String> state =
                new ExperimentState<>();

        ExperimentResult<String> first =
                createResult(
                        0.50,
                        "solution-1"
                );

        ExperimentResult<String> second =
                createResult(
                        0.40,
                        "solution-2"
                );

        state.register(first);

        boolean improved =
                state.register(second);

        assertTrue(improved);

        assertSame(
                second,
                state.bestOverallResult()
        );

        assertEquals(
                2,
                state.completedRuns()
        );
    }

    @Test
    void worseResultShouldNotReplaceBestOverallResult() {

        ExperimentState<String> state =
                new ExperimentState<>();

        ExperimentResult<String> first =
                createResult(
                        0.40,
                        "solution-1"
                );

        ExperimentResult<String> second =
                createResult(
                        0.50,
                        "solution-2"
                );

        state.register(first);

        boolean improved =
                state.register(second);

        assertFalse(improved);

        assertSame(
                first,
                state.bestOverallResult()
        );
    }

    @Test
    void nextGenerationShouldIncrementGeneration() {

        ExperimentState<String> state =
                new ExperimentState<>();

        assertEquals(
                0,
                state.generation()
        );

        state.nextGeneration();

        assertEquals(
                1,
                state.generation()
        );
    }

    private ExperimentResult<String> createResult(
            double bestCost,
            String bestSolution
    ) {

        return new ExperimentResult<>(
                0,
                123L,
                246L,
                247L,
                PARAMETERS,
                0.5,
                0.80,
                bestCost,
                0.60,
                bestSolution,
                "final-solution",
                1000,
                500,
                30,
                1_000_000L
        );
    }
}