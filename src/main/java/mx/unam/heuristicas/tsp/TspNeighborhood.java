package mx.unam.heuristicas.tsp;

import mx.unam.heuristicas.heuristic.Neighborhood;

import java.util.Objects;
import java.util.random.RandomGenerator;

public final class TspNeighborhood
        implements Neighborhood<TspSolution> {

    @Override
    public TspSolution generateNeighbor(
            TspSolution solution,
            RandomGenerator random
    ) {

        Objects.requireNonNull(
                solution,
                "La solución no puede ser null"
        );

        Objects.requireNonNull(
                random,
                "El generador pseudoaleatorio no puede ser null"
        );

        int size = solution.size();

        int firstIndex =
                random.nextInt(size);

        int secondIndex =
                random.nextInt(size - 1);

        if (secondIndex >= firstIndex) {
            secondIndex++;
        }

        int[] permutation =
                solution.toArray();

        int temporary =
                permutation[firstIndex];

        permutation[firstIndex] =
                permutation[secondIndex];

        permutation[secondIndex] =
                temporary;

        return new TspSolution(
                permutation
        );
    }
}