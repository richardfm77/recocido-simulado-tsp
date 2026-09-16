package mx.unam.heuristicas.heuristic.experiment;

import mx.unam.heuristicas.util.DoublePrecision;

import java.util.Objects;

public final class ExperimentState<S> {

    private int generation;
    private long completedRuns;

    private ExperimentResult<S> bestOverallResult;

    public ExperimentState() {
        this.generation = 0;
        this.completedRuns = 0;
        this.bestOverallResult = null;
    }

    public boolean register(
            ExperimentResult<S> result
    ) {

        Objects.requireNonNull(
                result,
                "El resultado experimental no puede ser null"
        );

        completedRuns++;

        if (bestOverallResult == null) {

            bestOverallResult = result;

            return true;
        }

        if (DoublePrecision.lessThan(
                result.bestCost(),
                bestOverallResult.bestCost()
        )) {

            bestOverallResult = result;

            return true;
        }

        return false;
    }

    public void nextGeneration() {
        generation++;
    }

    public int generation() {
        return generation;
    }

    public long completedRuns() {
        return completedRuns;
    }

    public boolean hasBestOverallResult() {
        return bestOverallResult != null;
    }

    public ExperimentResult<S> bestOverallResult() {

        if (bestOverallResult == null) {
            throw new IllegalStateException(
                    "Todavía no existe un mejor resultado experimental"
            );
        }

        return bestOverallResult;
    }
}