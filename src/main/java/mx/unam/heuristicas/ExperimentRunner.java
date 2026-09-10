package mx.unam.heuristicas;

import mx.unam.heuristicas.heuristic.experiment.*;
import mx.unam.heuristicas.report.ExperimentReportWriter;
import mx.unam.heuristicas.config.DatabaseConfig;
import mx.unam.heuristicas.config.DatabaseConnection;
import mx.unam.heuristicas.dao.CityDAO;
import mx.unam.heuristicas.dao.ConnectionDAO;
import mx.unam.heuristicas.dao.jdbc.JdbcCityDAO;
import mx.unam.heuristicas.dao.jdbc.JdbcConnectionDAO;
import mx.unam.heuristicas.heuristic.OptimizationResult;
import mx.unam.heuristicas.heuristic.ThresholdAccepting;
import mx.unam.heuristicas.heuristic.ThresholdAcceptingParameters;
import mx.unam.heuristicas.tsp.TspCostFunction;
import mx.unam.heuristicas.tsp.TspFileReader;
import mx.unam.heuristicas.tsp.TspInstance;
import mx.unam.heuristicas.tsp.TspInstanceFactory;
import mx.unam.heuristicas.tsp.TspNeighborhood;
import mx.unam.heuristicas.tsp.TspSolution;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class ExperimentRunner {

        private ExperimentRunner() {
        }

        public static void runExperiment(Path tspPath, Path propertiesPath, Path reportDirectory) {

                Objects.requireNonNull(
                                tspPath,
                                "El archivo TSP no puede ser null");
                Objects.requireNonNull(
                                propertiesPath,
                                "El archivo de propiedades no puede ser null");
                Objects.requireNonNull(
                                reportDirectory,
                                "El directorio de reportes no puede ser null");

                ExperimentConfig config = ExperimentConfig.load(propertiesPath);

                int[] cityIds = TspFileReader.read(
                                tspPath);

                DatabaseConnection databaseConnection = new DatabaseConnection(
                                new DatabaseConfig("application.properties"));

                CityDAO cityDAO = new JdbcCityDAO(
                                databaseConnection);

                ConnectionDAO connectionDAO = new JdbcConnectionDAO(
                                databaseConnection);

                TspInstanceFactory factory = new TspInstanceFactory(
                                cityDAO,
                                connectionDAO);

                TspInstance instance = factory.create(
                                cityIds);

                TspSolution initialSolution = createInitialSolution(
                                instance);

                TspCostFunction objectiveFunction = new TspCostFunction(
                                instance);

                TspNeighborhood neighborhood = new TspNeighborhood();

                ParameterGenerator parameterGenerator = new ParameterGenerator(
                                config);

                ExperimentState<TspSolution> state = new ExperimentState<>();

                List<ExperimentParameters> candidates = parameterGenerator.generateInitial();

                ExperimentReportWriter<TspSolution> reportWriter = new ExperimentReportWriter<>(
                                reportDirectory,
                                solution -> formatSolution(
                                                instance,
                                                solution));

                while (state.generation() < config.generations()) {

                        int generation = state.generation();

                        System.out.println();
                        System.out.println(
                                        "========================================");
                        System.out.println(
                                        "Generación experimental: "
                                                        + generation);
                        System.out.println(
                                        "========================================");

                        List<ConfigurationResult<TspSolution>> configurationResults = evaluateGeneration(
                                        generation,
                                        initialSolution,
                                        candidates,
                                        config,
                                        objectiveFunction,
                                        neighborhood,
                                        state,
                                        reportWriter);

                        ConfigurationResult<TspSolution> champion = selectChampion(
                                        configurationResults);

                        reportWriter.writeGenerationSummary(
                                        generation,
                                        champion);

                        printGenerationSummary(
                                        generation,
                                        champion);

                        state.nextGeneration();

                        if (state.generation() < config.generations()) {

                                candidates = parameterGenerator.generateAround(
                                                champion.parameters());
                        }
                }

                reportWriter.close();

                printFinalSummary(
                                state);
        }

        private static List<ConfigurationResult<TspSolution>> evaluateGeneration(
                        int generation,
                        TspSolution initialSolution,
                        List<ExperimentParameters> candidates,
                        ExperimentConfig config,
                        TspCostFunction objectiveFunction,
                        TspNeighborhood neighborhood,
                        ExperimentState<TspSolution> state,
                        ExperimentReportWriter<TspSolution> reportWriter) {

                List<ConfigurationResult<TspSolution>> configurationResults = new ArrayList<>(
                                candidates.size());

                for (ExperimentParameters parameters : candidates) {

                        ConfigurationResult<TspSolution> configurationResult = evaluateConfiguration(
                                        generation,
                                        initialSolution,
                                        parameters,
                                        config,
                                        objectiveFunction,
                                        neighborhood,
                                        state,
                                        reportWriter);
                        configurationResults.add(
                                        configurationResult);
                }

                return configurationResults;
        }

        private static ConfigurationResult<TspSolution> evaluateConfiguration(
                        int generation,
                        TspSolution initialSolution,
                        ExperimentParameters parameters,
                        ExperimentConfig config,
                        TspCostFunction objectiveFunction,
                        TspNeighborhood neighborhood,
                        ExperimentState<TspSolution> state,
                        ExperimentReportWriter<TspSolution> reportWriter) {

                long[] seeds = config.seeds();

                List<ExperimentResult<TspSolution>> results = new ArrayList<>(
                                seeds.length);

                for (long baseSeed : seeds) {

                        ExperimentResult<TspSolution> result = executeRun(
                                        generation,
                                        initialSolution,
                                        parameters,
                                        baseSeed,
                                        objectiveFunction,
                                        neighborhood);

                        reportWriter.writeRun(result);

                        results.add(
                                        result);

                        boolean newBest = state.register(
                                        result);

                        if (newBest) {

                                reportWriter.writeBestSolution(result);

                                System.out.println(
                                                "Nuevo mejor global"
                                                                + " | costo = "
                                                                + result.bestCost()
                                                                + " | T0 = "
                                                                + result.initialTemperature()
                                                                + " | seed = "
                                                                + result.baseSeed());
                        }
                }

                return new ConfigurationResult<>(
                                parameters,
                                results);
        }

        private static ExperimentResult<TspSolution> executeRun(
                        int generation,
                        TspSolution initialSolution,
                        ExperimentParameters parameters,
                        long baseSeed,
                        TspCostFunction objectiveFunction,
                        TspNeighborhood neighborhood) {

                long temperatureSeed = deriveTemperatureSeed(
                                baseSeed);

                long executionSeed = deriveExecutionSeed(
                                baseSeed);

                InitialTemperatureParameters temperatureParameters = new InitialTemperatureParameters(
                                parameters.initialTemperatureGuess(),
                                parameters.targetAcceptance(),
                                parameters.acceptanceEpsilon(),
                                parameters.temperatureSamples(),
                                parameters.temperatureMaxIterations());

                InitialTemperatureCalculator<TspSolution> temperatureCalculator = new InitialTemperatureCalculator<>(
                                objectiveFunction,
                                neighborhood,
                                temperatureParameters);

                double initialTemperature = temperatureCalculator.calculate(
                                initialSolution,
                                temperatureSeed);

                ThresholdAcceptingParameters heuristicParameters = new ThresholdAcceptingParameters(
                                initialTemperature,
                                parameters.temperatureEpsilon(),
                                parameters.coolingFactor(),
                                parameters.batchSize(),
                                parameters.maxAttemptsPerBatch());

                ThresholdAccepting<TspSolution> heuristic = new ThresholdAccepting<>(
                                objectiveFunction,
                                neighborhood,
                                heuristicParameters);

                double initialCost = objectiveFunction.evaluate(
                                initialSolution);

                long start = System.nanoTime();

                OptimizationResult<TspSolution> optimizationResult = heuristic.optimize(
                                initialSolution,
                                executionSeed);

                long elapsedNanos = System.nanoTime()
                                - start;

                return new ExperimentResult<>(
                                generation,
                                baseSeed,
                                temperatureSeed,
                                executionSeed,
                                parameters,
                                initialTemperature,
                                initialCost,
                                optimizationResult.bestCost(),
                                optimizationResult.finalCost(),
                                optimizationResult.bestSolution(),
                                optimizationResult.finalSolution(),
                                optimizationResult.generatedNeighbors(),
                                optimizationResult.acceptedNeighbors(),
                                optimizationResult.temperatureLevels(),
                                elapsedNanos);
        }

        private static ConfigurationResult<TspSolution> selectChampion(
                        List<ConfigurationResult<TspSolution>> results) {

                if (results.isEmpty()) {
                        throw new IllegalStateException(
                                        "No existen configuraciones evaluadas");
                }

                return results.stream()
                                .min(
                                                Comparator
                                                                .comparingDouble(
                                                                                ConfigurationResult<TspSolution>::meanBestCost)
                                                                .thenComparingDouble(
                                                                                ConfigurationResult<TspSolution>::standardDeviationBestCost)
                                                                .thenComparingDouble(
                                                                                ConfigurationResult<TspSolution>::meanElapsedNanos))
                                .orElseThrow();
        }

        private static long deriveTemperatureSeed(
                        long baseSeed) {

                return Math.multiplyExact(
                                baseSeed,
                                2L);
        }

        private static long deriveExecutionSeed(
                        long baseSeed) {

                return Math.addExact(
                                Math.multiplyExact(
                                                baseSeed,
                                                2L),
                                1L);
        }

        private static TspSolution createInitialSolution(
                        TspInstance instance) {

                int size = instance.size();

                int[] permutation = new int[size];

                for (int i = 0; i < size; i++) {
                        permutation[i] = i;
                }

                return new TspSolution(
                                permutation);
        }

        private static void printGenerationSummary(
                        int generation,
                        ConfigurationResult<TspSolution> champion) {

                System.out.println();
                System.out.println(
                                "Campeón de la generación "
                                                + generation);

                System.out.println(
                                "Media del mejor costo: "
                                                + champion.meanBestCost());

                System.out.println(
                                "Mediana: "
                                                + champion.medianBestCost());

                System.out.println(
                                "Desviación estándar: "
                                                + champion.standardDeviationBestCost());

                System.out.println(
                                "Mejor corrida: "
                                                + champion.bestCost());

                System.out.println(
                                "Peor corrida: "
                                                + champion.worstCost());

                System.out.println(
                                "Parámetros: "
                                                + champion.parameters());
        }

        private static void printFinalSummary(
                        ExperimentState<TspSolution> state) {

                System.out.println();
                System.out.println(
                                "========================================");

                System.out.println(
                                "Experimento terminado");

                System.out.println(
                                "Corridas ejecutadas: "
                                                + state.completedRuns());

                if (state.hasBestOverallResult()) {

                        ExperimentResult<TspSolution> best = state.bestOverallResult();

                        System.out.println(
                                        "Mejor costo global encontrado: "
                                                        + best.bestCost());

                        System.out.println(
                                        "Generación: "
                                                        + best.generation());

                        System.out.println(
                                        "Seed: "
                                                        + best.baseSeed());

                        System.out.println(
                                        "Parámetros: "
                                                        + best.parameters());

                        System.out.println(
                                        "Solución: "
                                                        + best.bestSolution());
                }

                System.out.println(
                                "========================================");
        }

        private static String formatSolution(
                        TspInstance instance,
                        TspSolution solution) {
                StringBuilder builder = new StringBuilder();

                builder.append("[");

                for (int i = 0; i < solution.size(); i++) {

                        if (i > 0) {
                                builder.append(", ");
                        }

                        int internalIndex = solution.get(i);

                        int cityId = instance.getCityId(internalIndex);

                        builder.append(cityId);
                }

                builder.append("]");

                return builder.toString();
        }
}