package mx.unam.heuristicas.heuristic;

@FunctionalInterface
public interface ObjectiveFunction<S> {

    double evaluate(S solution);
}