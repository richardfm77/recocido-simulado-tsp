package mx.unam.heuristicas.heuristic;

public record OptimizationResult<S>(
        S bestSolution,
        double bestCost,
        S finalSolution,
        double finalCost,
        long seed,
        long generatedNeighbors,
        long acceptedNeighbors,
        int temperatureLevels
) {
}