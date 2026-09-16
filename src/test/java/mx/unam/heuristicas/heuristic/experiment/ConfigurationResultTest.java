package mx.unam.heuristicas.heuristic.experiment;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationResultTest {

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
    void shouldCalculateStatistics() {

        List<ExperimentResult<String>> results =
                List.of(
                        createResult(
                                1L,
                                0.40,
                                100L
                        ),
                        createResult(
                                2L,
                                0.20,
                                200L
                        ),
                        createResult(
                                3L,
                                0.30,
                                300L
                        )
                );

        ConfigurationResult<String> configuration =
                new ConfigurationResult<>(
                        PARAMETERS,
                        results
                );

        assertEquals(
                3,
                configuration.numberOfRuns()
        );

        assertEquals(
                0.30,
                configuration.meanBestCost(),
                1e-7
        );

        assertEquals(
                0.30,
                configuration.medianBestCost(),
                1e-7
        );

        assertEquals(
                0.20,
                configuration.bestCost(),
                1e-7
        );

        assertEquals(
                0.40,
                configuration.worstCost(),
                1e-7
        );

        assertEquals(
                200.0,
                configuration.meanElapsedNanos(),
                1e-7
        );

        assertEquals(
                2L,
                configuration.bestRun()
                        .baseSeed()
        );
    }

    @Test
    void shouldCalculateMedianWithEvenNumberOfRuns() {

        List<ExperimentResult<String>> results =
                List.of(
                        createResult(
                                1L,
                                0.10,
                                100L
                        ),
                        createResult(
                                2L,
                                0.20,
                                100L
                        ),
                        createResult(
                                3L,
                                0.30,
                                100L
                        ),
                        createResult(
                                4L,
                                0.40,
                                100L
                        )
                );

        ConfigurationResult<String> configuration =
                new ConfigurationResult<>(
                        PARAMETERS,
                        results
                );

        assertEquals(
                0.25,
                configuration.medianBestCost(),
                1e-7
        );
    }

    @Test
    void singleRunShouldHaveZeroStandardDeviation() {

        ConfigurationResult<String> configuration =
                new ConfigurationResult<>(
                        PARAMETERS,
                        List.of(
                                createResult(
                                        1L,
                                        0.30,
                                        100L
                                )
                        )
                );

        assertEquals(
                0.0,
                configuration.standardDeviationBestCost(),
                1e-7
        );
    }

    @Test
    void shouldCalculateStandardDeviation() {

        List<ExperimentResult<String>> results =
                List.of(
                        createResult(
                                1L,
                                0.20,
                                100L
                        ),
                        createResult(
                                2L,
                                0.30,
                                100L
                        ),
                        createResult(
                                3L,
                                0.40,
                                100L
                        )
                );

        ConfigurationResult<String> configuration =
                new ConfigurationResult<>(
                        PARAMETERS,
                        results
                );

        double expected =
                Math.sqrt(
                        (
                                Math.pow(0.20 - 0.30, 2)
                                +
                                Math.pow(0.30 - 0.30, 2)
                                +
                                Math.pow(0.40 - 0.30, 2)
                        )
                        / 3.0
                );

        assertEquals(
                expected,
                configuration.standardDeviationBestCost(),
                1e-7
        );
    }

    @Test
    void shouldRejectEmptyResults() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new ConfigurationResult<>(
                                PARAMETERS,
                                List.of()
                        )
        );
    }

    @Test
    void shouldRejectResultsFromDifferentConfiguration() {

        ExperimentParameters otherParameters =
                new ExperimentParameters(
                        0.90,
                        1e-5,
                        100,
                        10,
                        1.0,
                        0.90,
                        0.01,
                        1000,
                        100
                );

        ExperimentResult<String> result =
                createResult(
                        otherParameters,
                        1L,
                        0.30,
                        100L
                );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new ConfigurationResult<>(
                                PARAMETERS,
                                List.of(result)
                        )
        );
    }

    private static ExperimentResult<String> createResult(
            long seed,
            double bestCost,
            long elapsedNanos
    ) {

        return createResult(
                PARAMETERS,
                seed,
                bestCost,
                elapsedNanos
        );
    }

    private static ExperimentResult<String> createResult(
            ExperimentParameters parameters,
            long seed,
            double bestCost,
            long elapsedNanos
    ) {

        return new ExperimentResult<>(
                0,
                seed,
                seed * 2,
                seed * 2 + 1,
                parameters,
                0.5,
                0.80,
                bestCost,
                0.60,
                "best-" + seed,
                "final-" + seed,
                1000,
                500,
                30,
                elapsedNanos
        );
    }
}