package mx.unam.heuristicas.report;

import mx.unam.heuristicas.exception.AppException;
import mx.unam.heuristicas.heuristic.experiment.ConfigurationResult;
import mx.unam.heuristicas.heuristic.experiment.ExperimentParameters;
import mx.unam.heuristicas.heuristic.experiment.ExperimentResult;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.function.Function;

public final class ExperimentReportWriter<S>
        implements AutoCloseable {

    private final BufferedWriter runsWriter;
    private final BufferedWriter bestSolutionsWriter;
    private final BufferedWriter generationSummaryWriter;
    private final Function<S, String> solutionFormatter;

    public ExperimentReportWriter(
            Path reportDirectory,
            Function<S, String> solutionFormatter) {
        Objects.requireNonNull(
                reportDirectory,
                "El directorio de reportes no puede ser null");

        this.solutionFormatter = Objects.requireNonNull(
                solutionFormatter,
                "El formateador de soluciones no puede ser null");

        try {
            Files.createDirectories(reportDirectory);

            Path runsPath = reportDirectory.resolve("experiment-runs.csv");

            Path bestSolutionsPath = reportDirectory.resolve("best-solutions.csv");

            Path generationSummaryPath = reportDirectory.resolve("generation-summary.csv");

            runsWriter = Files.newBufferedWriter(
                    runsPath,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            bestSolutionsWriter = Files.newBufferedWriter(
                    bestSolutionsPath,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            generationSummaryWriter = Files.newBufferedWriter(
                    generationSummaryPath,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            writeHeaders();

        } catch (IOException e) {
            throw new AppException(
                    "No se pudieron crear los archivos de reporte",
                    e);
        }
    }

    public void writeRun(ExperimentResult<S> result) {
        Objects.requireNonNull(
                result,
                "El resultado experimental no puede ser null");

        ExperimentParameters p = result.parameters();

        try {
            runsWriter.write(
                    result.generation()
                            + "," + result.baseSeed()
                            + "," + result.temperatureSeed()
                            + "," + result.executionSeed()
                            + "," + result.initialTemperature()
                            + "," + result.initialCost()
                            + "," + result.bestCost()
                            + "," + result.finalCost()
                            + "," + p.coolingFactor()
                            + "," + p.temperatureEpsilon()
                            + "," + p.batchSize()
                            + "," + p.maxAttemptFactor()
                            + "," + p.maxAttemptsPerBatch()
                            + "," + p.targetAcceptance()
                            + "," + p.acceptanceEpsilon()
                            + "," + p.temperatureSamples()
                            + "," + p.temperatureMaxIterations()
                            + "," + result.generatedNeighbors()
                            + "," + result.acceptedNeighbors()
                            + "," + result.temperatureLevels()
                            + "," + result.elapsedNanos());

            runsWriter.newLine();
            runsWriter.flush();

        } catch (IOException e) {
            throw new AppException(
                    "No se pudo escribir el resultado experimental",
                    e);
        }
    }

    public void writeGenerationSummary(
            int generation,
            ConfigurationResult<S> champion) {
        Objects.requireNonNull(
                champion,
                "El campeón de la generación no puede ser null");

        ExperimentParameters p = champion.parameters();

        try {
            generationSummaryWriter.write(
                    generation
                            + "," + champion.meanBestCost()
                            + "," + champion.medianBestCost()
                            + "," + champion.standardDeviationBestCost()
                            + "," + champion.bestCost()
                            + "," + champion.worstCost()
                            + "," + champion.meanElapsedNanos()
                            + "," + p.coolingFactor()
                            + "," + p.temperatureEpsilon()
                            + "," + p.batchSize()
                            + "," + p.maxAttemptFactor()
                            + "," + p.maxAttemptsPerBatch()
                            + "," + p.initialTemperatureGuess()
                            + "," + p.targetAcceptance()
                            + "," + p.acceptanceEpsilon()
                            + "," + p.temperatureSamples()
                            + "," + p.temperatureMaxIterations());

            generationSummaryWriter.newLine();
            generationSummaryWriter.flush();

        } catch (IOException e) {
            throw new AppException(
                    "No se pudo escribir el resumen de la generación",
                    e);
        }
    }

    public void writeBestSolution(ExperimentResult<S> result) {
        Objects.requireNonNull(
                result,
                "El resultado experimental no puede ser null");

        ExperimentParameters p = result.parameters();

        String solution = escapeCsv(
                solutionFormatter.apply(
                        result.bestSolution()));

        try {
            bestSolutionsWriter.write(
                    result.generation()
                            + "," + result.baseSeed()
                            + "," + result.temperatureSeed()
                            + "," + result.executionSeed()
                            + "," + result.initialTemperature()
                            + "," + result.bestCost()
                            + "," + p.coolingFactor()
                            + "," + p.temperatureEpsilon()
                            + "," + p.batchSize()
                            + "," + p.maxAttemptFactor()
                            + "," + p.maxAttemptsPerBatch()
                            + "," + p.targetAcceptance()
                            + "," + solution);

            bestSolutionsWriter.newLine();
            bestSolutionsWriter.flush();

        } catch (IOException e) {
            throw new AppException(
                    "No se pudo escribir la mejor solución",
                    e);
        }
    }

    private void writeHeaders() throws IOException {

        runsWriter.write(
                "generation,"
                        + "baseSeed,"
                        + "temperatureSeed,"
                        + "executionSeed,"
                        + "initialTemperature,"
                        + "initialCost,"
                        + "bestCost,"
                        + "finalCost,"
                        + "coolingFactor,"
                        + "temperatureEpsilon,"
                        + "batchSize,"
                        + "maxAttemptFactor,"
                        + "maxAttemptsPerBatch,"
                        + "targetAcceptance,"
                        + "acceptanceEpsilon,"
                        + "temperatureSamples,"
                        + "temperatureMaxIterations,"
                        + "generatedNeighbors,"
                        + "acceptedNeighbors,"
                        + "temperatureLevels,"
                        + "elapsedNanos");

        runsWriter.newLine();

        bestSolutionsWriter.write(
                "generation,"
                        + "baseSeed,"
                        + "temperatureSeed,"
                        + "executionSeed,"
                        + "initialTemperature,"
                        + "bestCost,"
                        + "coolingFactor,"
                        + "temperatureEpsilon,"
                        + "batchSize,"
                        + "maxAttemptFactor,"
                        + "maxAttemptsPerBatch,"
                        + "targetAcceptance,"
                        + "bestSolution");

        bestSolutionsWriter.newLine();

        generationSummaryWriter.write(
                "generation,"
                        + "meanBestCost,"
                        + "medianBestCost,"
                        + "standardDeviationBestCost,"
                        + "bestCost,"
                        + "worstCost,"
                        + "meanElapsedNanos,"
                        + "coolingFactor,"
                        + "temperatureEpsilon,"
                        + "batchSize,"
                        + "maxAttemptFactor,"
                        + "maxAttemptsPerBatch,"
                        + "initialTemperatureGuess,"
                        + "targetAcceptance,"
                        + "acceptanceEpsilon,"
                        + "temperatureSamples,"
                        + "temperatureMaxIterations");

        generationSummaryWriter.newLine();
    }

    private String escapeCsv(String value) {

        if (value == null) {
            return "";
        }

        String escaped = value.replace("\"", "\"\"");

        return "\"" + escaped + "\"";
    }

    @Override
    public void close() {

        try {
            runsWriter.close();
            bestSolutionsWriter.close();
            generationSummaryWriter.close();
        } catch (IOException e) {
            throw new AppException(
                    "No se pudieron cerrar los archivos de reporte",
                    e);
        }
    }
}