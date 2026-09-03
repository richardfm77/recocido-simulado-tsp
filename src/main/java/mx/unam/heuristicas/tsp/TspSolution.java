package mx.unam.heuristicas.tsp;

import java.util.Arrays;
import java.util.Objects;

public final class TspSolution {

    private final int[] permutation;

    public TspSolution(int[] permutation) {
        Objects.requireNonNull(
                permutation,
                "La permutación no puede ser null");

        if (permutation.length < 2) {
            throw new IllegalArgumentException(
                    "Una solución TSP debe contener al menos dos ciudades");
        }

        this.permutation = permutation;
    }

    public int size() {
        return permutation.length;
    }

    public int get(int index) {
        return permutation[index];
    }

    public int[] toArray() {
        return permutation.clone();
    }

    @Override
    public String toString() {
        return Arrays.toString(permutation);
    }

    public static TspSolution initial(int size) {

        if (size < 2) {
            throw new IllegalArgumentException(
                    "Una solución TSP debe contener al menos dos ciudades");
        }

        int[] permutation = new int[size];

        for (int i = 0; i < size; i++) {
            permutation[i] = i;
        }

        return new TspSolution(
                permutation);
    }
}