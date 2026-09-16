package mx.unam.heuristicas.util;

public final class DoublePrecision {

    /**
     * Tolerancia numérica utilizada en operaciones con double.
     *
     * 1e-7 = 0.0000001
     */
    public static final double TOLERANCE = 1e-7;

    private DoublePrecision() {
    }

    public static boolean equals(double a, double b) {
        return Math.abs(a - b) <= TOLERANCE;
    }

    public static boolean lessThanOrEqual(double a, double b) {
        return a <= b + TOLERANCE;
    }

    public static boolean greaterThan(double a, double b) {
        return a > b + TOLERANCE;
    }

    public static boolean lessThan(double a, double b) {
        return a < b - TOLERANCE;
    }
}