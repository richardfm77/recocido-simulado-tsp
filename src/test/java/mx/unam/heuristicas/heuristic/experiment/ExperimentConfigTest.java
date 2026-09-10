package mx.unam.heuristicas.heuristic.experiment;

import mx.unam.heuristicas.exception.AppException;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class ExperimentConfigTest {

    @Test
    void shouldLoadValidProperties() {

        Properties properties =
                validProperties();

        ExperimentConfig config =
                ExperimentConfig.from(
                        properties
                );

        assertEquals(
                20,
                config.generations()
        );

        assertEquals(
                10,
                config.candidatesPerGeneration()
        );

        assertArrayEquals(
                new long[]{
                        123L,
                        456L,
                        789L
                },
                config.seeds()
        );

        assertEquals(
                0.80,
                config.coolingFactorMin()
        );

        assertEquals(
                0.99,
                config.coolingFactorMax()
        );

        assertEquals(
                20,
                config.batchSizeMin()
        );

        assertEquals(
                200,
                config.batchSizeMax()
        );

        assertEquals(
                0.20,
                config.explorationProbability()
        );
    }

    @Test
    void shouldRejectMissingProperty() {

        Properties properties =
                validProperties();

        properties.remove(
                "experiment.generations"
        );

        assertThrows(
                AppException.class,
                () ->
                        ExperimentConfig.from(
                                properties
                        )
        );
    }

    @Test
    void shouldRejectInvalidRange() {

        Properties properties =
                validProperties();

        properties.setProperty(
                "experiment.cooling-factor.min",
                "0.99"
        );

        properties.setProperty(
                "experiment.cooling-factor.max",
                "0.80"
        );

        assertThrows(
                AppException.class,
                () ->
                        ExperimentConfig.from(
                                properties
                        )
        );
    }

    @Test
    void shouldRejectInvalidExplorationProbability() {

        Properties properties =
                validProperties();

        properties.setProperty(
                "experiment.exploration-probability",
                "1.5"
        );

        assertThrows(
                AppException.class,
                () ->
                        ExperimentConfig.from(
                                properties
                        )
        );
    }

    @Test
    void seedsShouldBeDefensiveCopy() {

        ExperimentConfig config =
                ExperimentConfig.from(
                        validProperties()
                );

        long[] seeds =
                config.seeds();

        seeds[0] =
                999999L;

        assertEquals(
                123L,
                config.seeds()[0]
        );
    }

    private static Properties validProperties() {

        Properties properties =
                new Properties();

        properties.setProperty(
                "experiment.generations",
                "20"
        );

        properties.setProperty(
                "experiment.candidates-per-generation",
                "10"
        );

        properties.setProperty(
                "experiment.seeds",
                "123,456,789"
        );

        properties.setProperty(
                "experiment.cooling-factor.min",
                "0.80"
        );

        properties.setProperty(
                "experiment.cooling-factor.max",
                "0.99"
        );

        properties.setProperty(
                "experiment.temperature-epsilon.min",
                "0.000001"
        );

        properties.setProperty(
                "experiment.temperature-epsilon.max",
                "0.01"
        );

        properties.setProperty(
                "experiment.batch-size.min",
                "20"
        );

        properties.setProperty(
                "experiment.batch-size.max",
                "200"
        );

        properties.setProperty(
                "experiment.max-attempt-factor.min",
                "5"
        );

        properties.setProperty(
                "experiment.max-attempt-factor.max",
                "20"
        );

        properties.setProperty(
                "experiment.initial-temperature.guess",
                "1.0"
        );

        properties.setProperty(
                "experiment.target-acceptance.min",
                "0.85"
        );

        properties.setProperty(
                "experiment.target-acceptance.max",
                "0.95"
        );

        properties.setProperty(
                "experiment.acceptance-epsilon",
                "0.01"
        );

        properties.setProperty(
                "experiment.temperature-samples",
                "1000"
        );

        properties.setProperty(
                "experiment.temperature-max-iterations",
                "100"
        );

        properties.setProperty(
                "experiment.exploration-probability",
                "0.20"
        );

        properties.setProperty(
                "experiment.variation-factor",
                "0.10"
        );

        properties.setProperty(
                "experiment.parameter-seed",
                "20260910"
        );

        return properties;
    }
}