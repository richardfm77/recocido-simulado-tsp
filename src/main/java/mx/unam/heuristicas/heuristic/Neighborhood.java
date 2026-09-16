package mx.unam.heuristicas.heuristic;

import java.util.random.RandomGenerator;

@FunctionalInterface
public interface Neighborhood<S> {

    S generateNeighbor(
            S solution,
            RandomGenerator random
    );
}