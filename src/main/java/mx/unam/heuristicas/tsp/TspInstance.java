package mx.unam.heuristicas.tsp;

import java.util.Objects;

public final class TspInstance {

    private final int[] cityIds;
    private final double[][] weights;
    private final double normalizer;

    public TspInstance(
            int[] cityIds,
            double[][] weights,
            double normalizer
    ) {
        Objects.requireNonNull(
                cityIds,
                "Los IDs de las ciudades no pueden ser null"
        );

        Objects.requireNonNull(
                weights,
                "La matriz de pesos no puede ser null"
        );

        if (cityIds.length < 2) {
            throw new IllegalArgumentException(
                    "Una instancia TSP debe contener al menos dos ciudades"
            );
        }

        if (weights.length != cityIds.length) {
            throw new IllegalArgumentException(
                    "La matriz de pesos debe tener el mismo tamaño que el número de ciudades"
            );
        }

        for (double[] row : weights) {
            if (row == null || row.length != cityIds.length) {
                throw new IllegalArgumentException(
                        "La matriz de pesos debe ser cuadrada"
                );
            }
        }

        if (normalizer <= 0.0 || !Double.isFinite(normalizer)) {
            throw new IllegalArgumentException(
                    "El normalizador debe ser positivo y finito"
            );
        }

        this.cityIds = cityIds.clone();
        this.weights = copyMatrix(weights);
        this.normalizer = normalizer;
    }

    public int size() {
        return cityIds.length;
    }

    public int getCityId(int index) {
        return cityIds[index];
    }

    public double getWeight(int fromIndex, int toIndex) {
        return weights[fromIndex][toIndex];
    }

    public double getNormalizer() {
        return normalizer;
    }

    private static double[][] copyMatrix(double[][] matrix) {

        double[][] copy =
                new double[matrix.length][];

        for (int i = 0; i < matrix.length; i++) {
            copy[i] = matrix[i].clone();
        }

        return copy;
    }
}