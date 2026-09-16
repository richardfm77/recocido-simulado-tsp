package mx.unam.heuristicas.heuristic.experiment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.random.RandomGenerator;

public final class ParameterGenerator {

        private final ExperimentConfig config;
        private final RandomGenerator random;

        public ParameterGenerator(
                        ExperimentConfig config) {

                this.config = Objects.requireNonNull(
                                config,
                                "La configuración experimental no puede ser null");

                this.random = new Random(
                                config.parameterSeed());
        }

        public List<ExperimentParameters> generateInitial() {

                List<ExperimentParameters> candidates = new ArrayList<>(
                                config.candidatesPerGeneration());

                for (int i = 0; i < config.candidatesPerGeneration(); i++) {

                        candidates.add(
                                        generateGlobal());
                }

                return candidates;
        }

        public List<ExperimentParameters> generateAround(
                        ExperimentParameters champion) {

                Objects.requireNonNull(
                                champion,
                                "Los parámetros campeones no pueden ser null");

                List<ExperimentParameters> candidates = new ArrayList<>(
                                config.candidatesPerGeneration());

                /*
                 * Conservamos al campeón.
                 *
                 * Esto evita perder la mejor configuración
                 * conocida entre generaciones.
                 */
                candidates.add(champion);

                while (candidates.size() < config.candidatesPerGeneration()) {

                        if (random.nextDouble() < config.explorationProbability()) {

                                candidates.add(
                                                generateGlobal());

                        } else {

                                candidates.add(
                                                generateNeighbor(
                                                                champion));
                        }
                }

                return candidates;
        }

        private ExperimentParameters generateGlobal() {

                return new ExperimentParameters(
                                randomDouble(
                                                config.coolingFactorMin(),
                                                config.coolingFactorMax()),
                                randomLogarithmic(
                                                config.temperatureEpsilonMin(),
                                                config.temperatureEpsilonMax()),
                                randomInt(
                                                config.batchSizeMin(),
                                                config.batchSizeMax()),
                                randomInt(
                                                config.maxAttemptFactorMin(),
                                                config.maxAttemptFactorMax()),
                                config.initialTemperatureGuess(),
                                randomDouble(
                                                config.targetAcceptanceMin(),
                                                config.targetAcceptanceMax()),
                                config.acceptanceEpsilon(),
                                config.temperatureSamples(),
                                config.temperatureMaxIterations());
        }

        private ExperimentParameters generateNeighbor(
                        ExperimentParameters champion) {

                return new ExperimentParameters(
                                varyDouble(
                                                champion.coolingFactor(),
                                                config.coolingFactorMin(),
                                                config.coolingFactorMax()),
                                varyLogarithmic(
                                                champion.temperatureEpsilon(),
                                                config.temperatureEpsilonMin(),
                                                config.temperatureEpsilonMax()),
                                varyInt(
                                                champion.batchSize(),
                                                config.batchSizeMin(),
                                                config.batchSizeMax()),
                                varyInt(
                                                champion.maxAttemptFactor(),
                                                config.maxAttemptFactorMin(),
                                                config.maxAttemptFactorMax()),
                                config.initialTemperatureGuess(),
                                varyDouble(
                                                champion.targetAcceptance(),
                                                config.targetAcceptanceMin(),
                                                config.targetAcceptanceMax()),
                                config.acceptanceEpsilon(),
                                config.temperatureSamples(),
                                config.temperatureMaxIterations());
        }

        private double randomDouble(
                        double min,
                        double max) {

                if (min == max) {
                        return min;
                }

                return random.nextDouble(
                                min,
                                max);
        }

        private int randomInt(
                        int min,
                        int max) {

                if (min == max) {
                        return min;
                }

                return random.nextInt(
                                min,
                                max + 1);
        }

        private double randomLogarithmic(
                        double min,
                        double max) {

                if (min == max) {
                        return min;
                }

                double logMin = Math.log10(min);

                double logMax = Math.log10(max);

                double exponent = random.nextDouble(
                                logMin,
                                logMax);

                return Math.pow(
                                10.0,
                                exponent);
        }

        private double varyDouble(
                        double value,
                        double min,
                        double max) {

                double range = max - min;

                double maximumVariation = range
                                * config.variationFactor();

                double variation = random.nextDouble(
                                -maximumVariation,
                                maximumVariation);

                return clamp(
                                value + variation,
                                min,
                                max);
        }

        private int varyInt(
                        int value,
                        int min,
                        int max) {

                int range = max - min;

                int maximumVariation = Math.max(
                                1,
                                (int) Math.round(
                                                range
                                                                * config.variationFactor()));

                int variation = random.nextInt(
                                -maximumVariation,
                                maximumVariation + 1);

                return clamp(
                                value + variation,
                                min,
                                max);
        }

        private double varyLogarithmic(
                        double value,
                        double min,
                        double max) {

                double logValue = Math.log10(value);

                double logMin = Math.log10(min);

                double logMax = Math.log10(max);

                double logRange = logMax - logMin;

                double maximumVariation = logRange
                                * config.variationFactor();

                double variation = random.nextDouble(
                                -maximumVariation,
                                maximumVariation);

                double newLogValue = clamp(
                                logValue + variation,
                                logMin,
                                logMax);

                return Math.pow(
                                10.0,
                                newLogValue);
        }

        private static double clamp(
                        double value,
                        double min,
                        double max) {

                return Math.max(
                                min,
                                Math.min(
                                                max,
                                                value));
        }

        private static int clamp(
                        int value,
                        int min,
                        int max) {

                return Math.max(
                                min,
                                Math.min(
                                                max,
                                                value));
        }
}