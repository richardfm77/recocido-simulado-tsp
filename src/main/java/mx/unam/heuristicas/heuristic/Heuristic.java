package mx.unam.heuristicas.heuristic;

public interface Heuristic<S> {

    OptimizationResult<S> optimize(
            S initialSolution,
            long seed
    );
}