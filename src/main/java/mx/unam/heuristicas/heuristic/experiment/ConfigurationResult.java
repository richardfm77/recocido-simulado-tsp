package mx.unam.heuristicas.heuristic.experiment;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class ConfigurationResult<S> {

    private final ExperimentParameters parameters;
    private final List<ExperimentResult<S>> results;

    private final double meanBestCost;
    private final double medianBestCost;
    private final double standardDeviationBestCost;

    private final double bestCost;
    private final double worstCost;

    private final double meanElapsedNanos;

    private final ExperimentResult<S> bestRun;

    public ConfigurationResult(
            ExperimentParameters parameters,
            List<ExperimentResult<S>> results
    ) {

        this.parameters =
                Objects.requireNonNull(
                        parameters,
                        "Los parámetros no pueden ser null"
                );

        Objects.requireNonNull(
                results,
                "Los resultados no pueden ser null"
        );

        if (results.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe existir al menos un resultado"
            );
        }

        validateParameters(
                parameters,
                results
        );

        this.results =
                List.copyOf(results);

        this.meanBestCost =
                calculateMeanBestCost(
                        results
                );

        this.medianBestCost =
                calculateMedianBestCost(
                        results
                );

        this.standardDeviationBestCost =
                calculateStandardDeviationBestCost(
                        results,
                        meanBestCost
                );

        this.bestRun =
                findBestRun(
                        results
                );

        this.bestCost =
                bestRun.bestCost();

        this.worstCost =
                findWorstCost(
                        results
                );

        this.meanElapsedNanos =
                calculateMeanElapsedNanos(
                        results
                );
    }

    private static <S> void validateParameters(
            ExperimentParameters parameters,
            List<ExperimentResult<S>> results
    ) {

        for (ExperimentResult<S> result : results) {

            Objects.requireNonNull(
                    result,
                    "La lista de resultados no puede contener null"
            );

            if (!parameters.equals(
                    result.parameters()
            )) {

                throw new IllegalArgumentException(
                        "Todos los resultados deben pertenecer "
                                + "a la misma configuración"
                );
            }
        }
    }

    private static <S> double calculateMeanBestCost(
            List<ExperimentResult<S>> results
    ) {

        double sum = 0.0;

        for (ExperimentResult<S> result : results) {
            sum += result.bestCost();
        }

        return sum / results.size();
    }

    private static <S> double calculateMedianBestCost(
            List<ExperimentResult<S>> results
    ) {

        List<Double> costs =
                new ArrayList<>(
                        results.size()
                );

        for (ExperimentResult<S> result : results) {
            costs.add(
                    result.bestCost()
            );
        }

        costs.sort(
                Comparator.naturalOrder()
        );

        int size =
                costs.size();

        if (size % 2 == 1) {

            return costs.get(
                    size / 2
            );
        }

        double left =
                costs.get(
                        size / 2 - 1
                );

        double right =
                costs.get(
                        size / 2
                );

        return (left + right) / 2.0;
    }

    private static <S> double calculateStandardDeviationBestCost(
            List<ExperimentResult<S>> results,
            double mean
    ) {

        double sum =
                0.0;

        for (ExperimentResult<S> result : results) {

            double difference =
                    result.bestCost()
                            - mean;

            sum +=
                    difference
                            * difference;
        }

        return Math.sqrt(
                sum / results.size()
        );
    }

    private static <S> ExperimentResult<S> findBestRun(
            List<ExperimentResult<S>> results
    ) {

        ExperimentResult<S> best =
                results.get(0);

        for (int i = 1;
             i < results.size();
             i++) {

            ExperimentResult<S> current =
                    results.get(i);

            if (current.bestCost()
                    < best.bestCost()) {

                best = current;
            }
        }

        return best;
    }

    private static <S> double findWorstCost(
            List<ExperimentResult<S>> results
    ) {

        double worst =
                results.get(0)
                        .bestCost();

        for (int i = 1;
             i < results.size();
             i++) {

            double cost =
                    results.get(i)
                            .bestCost();

            if (cost > worst) {
                worst = cost;
            }
        }

        return worst;
    }

    private static <S> double calculateMeanElapsedNanos(
            List<ExperimentResult<S>> results
    ) {

        double sum =
                0.0;

        for (ExperimentResult<S> result : results) {
            sum +=
                    result.elapsedNanos();
        }

        return sum
                / results.size();
    }

    public ExperimentParameters parameters() {
        return parameters;
    }

    public List<ExperimentResult<S>> results() {
        return results;
    }

    public int numberOfRuns() {
        return results.size();
    }

    public double meanBestCost() {
        return meanBestCost;
    }

    public double medianBestCost() {
        return medianBestCost;
    }

    public double standardDeviationBestCost() {
        return standardDeviationBestCost;
    }

    public double bestCost() {
        return bestCost;
    }

    public double worstCost() {
        return worstCost;
    }

    public double meanElapsedNanos() {
        return meanElapsedNanos;
    }

    public ExperimentResult<S> bestRun() {
        return bestRun;
    }
}