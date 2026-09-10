package mx.unam.heuristicas.heuristic.experiment;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class ParameterGeneratorTest {

    @Test
    void initialGenerationShouldHaveConfiguredSize() {

        ExperimentConfig config =
                ExperimentConfig.from(
                        validProperties()
                );

        ParameterGenerator generator =
                new ParameterGenerator(config);

        List<ExperimentParameters> candidates =
                generator.generateInitial();

        assertEquals(
                config.candidatesPerGeneration(),
                candidates.size()
        );
    }

    @Test
    void initialGenerationShouldRespectRanges() {

        ExperimentConfig config =
                ExperimentConfig.from(
                        validProperties()
                );

        ParameterGenerator generator =
                new ParameterGenerator(config);

        List<ExperimentParameters> candidates =
                generator.generateInitial();

        for (ExperimentParameters parameters
                : candidates) {

            assertWithinRanges(
                    parameters,
                    config
            );
        }
    }

    @Test
    void generationAroundChampionShouldKeepChampion() {

        ExperimentConfig config =
                ExperimentConfig.from(
                        validProperties()
                );

        ParameterGenerator generator =
                new ParameterGenerator(config);

        ExperimentParameters champion =
                new ExperimentParameters(
                        0.95,
                        1e-4,
                        100,
                        10,
                        1.0,
                        0.90,
                        0.01,
                        1000,
                        100
                );

        List<ExperimentParameters> candidates =
                generator.generateAround(
                        champion
                );

        assertEquals(
                champion,
                candidates.get(0)
        );
    }

    @Test
    void generationAroundChampionShouldRespectRanges() {

        ExperimentConfig config =
                ExperimentConfig.from(
                        validProperties()
                );

        ParameterGenerator generator =
                new ParameterGenerator(config);

        ExperimentParameters champion =
                new ExperimentParameters(
                        0.95,
                        1e-4,
                        100,
                        10,
                        1.0,
                        0.90,
                        0.01,
                        1000,
                        100
                );

        List<ExperimentParameters> candidates =
                generator.generateAround(
                        champion
                );

        for (ExperimentParameters parameters
                : candidates) {

            assertWithinRanges(
                    parameters,
                    config
            );
        }
    }

    private static void assertWithinRanges(
            ExperimentParameters parameters,
            ExperimentConfig config
    ) {

        assertTrue(
                parameters.coolingFactor()
                        >= config.coolingFactorMin()
        );

        assertTrue(
                parameters.coolingFactor()
                        <= config.coolingFactorMax()
        );

        assertTrue(
                parameters.temperatureEpsilon()
                        >= config.temperatureEpsilonMin()
        );

        assertTrue(
                parameters.temperatureEpsilon()
                        <= config.temperatureEpsilonMax()
        );

        assertTrue(
                parameters.batchSize()
                        >= config.batchSizeMin()
        );

        assertTrue(
                parameters.batchSize()
                        <= config.batchSizeMax()
        );

        assertTrue(
                parameters.maxAttemptFactor()
                        >= config.maxAttemptFactorMin()
        );

        assertTrue(
                parameters.maxAttemptFactor()
                        <= config.maxAttemptFactorMax()
        );

        assertTrue(
                parameters.targetAcceptance()
                        >= config.targetAcceptanceMin()
        );

        assertTrue(
                parameters.targetAcceptance()
                        <= config.targetAcceptanceMax()
        );
    }

    private static Properties validProperties() {

        Properties properties =
                new Properties();

        properties.setProperty(
                "experiment.generations", "20"
        );

        properties.setProperty(
                "experiment.candidates-per-generation", "10"
        );

        properties.setProperty(
                "experiment.seeds", "123,456,789"
        );

        properties.setProperty(
                "experiment.cooling-factor.min", "0.80"
        );

        properties.setProperty(
                "experiment.cooling-factor.max", "0.99"
        );

        properties.setProperty(
                "experiment.temperature-epsilon.min", "0.000001"
        );

        properties.setProperty(
                "experiment.temperature-epsilon.max", "0.01"
        );

        properties.setProperty(
                "experiment.batch-size.min", "20"
        );

        properties.setProperty(
                "experiment.batch-size.max", "200"
        );

        properties.setProperty(
                "experiment.max-attempt-factor.min", "5"
        );

        properties.setProperty(
                "experiment.max-attempt-factor.max", "20"
        );

        properties.setProperty(
                "experiment.initial-temperature.guess", "1.0"
        );

        properties.setProperty(
                "experiment.target-acceptance.min", "0.85"
        );

        properties.setProperty(
                "experiment.target-acceptance.max", "0.95"
        );

        properties.setProperty(
                "experiment.acceptance-epsilon", "0.01"
        );

        properties.setProperty(
                "experiment.temperature-samples", "1000"
        );

        properties.setProperty(
                "experiment.temperature-max-iterations", "100"
        );

        properties.setProperty(
                "experiment.exploration-probability", "0.20"
        );

        properties.setProperty(
                "experiment.variation-factor", "0.10"
        );

        properties.setProperty(
                "experiment.parameter-seed", "20260910"
        );

        return properties;
    }
}