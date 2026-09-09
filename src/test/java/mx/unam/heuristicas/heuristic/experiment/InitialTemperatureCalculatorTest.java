package mx.unam.heuristicas.heuristic.experiment;

import org.junit.jupiter.api.Test;

import mx.unam.heuristicas.heuristic.Neighborhood;
import mx.unam.heuristicas.heuristic.ObjectiveFunction;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class InitialTemperatureCalculatorTest {

    private final ObjectiveFunction<Double> objectiveFunction =
            value -> value;

    @Test
    void acceptedPercentageShouldBeZeroWhenTemperatureIsTooLow() {

        /*
         * Cada vecino cuesta exactamente 1 más.
         *
         * Con T = 0.5:
         *
         * s + 1 <= s + 0.5
         *
         * siempre es falso.
         */
        Neighborhood<Double> neighborhood =
                (solution, random) ->
                        solution + 1.0;

        InitialTemperatureParameters parameters =
                new InitialTemperatureParameters(
                        0.5,
                        0.9,
                        0.01,
                        100,
                        100
                );

        InitialTemperatureCalculator<Double> calculator =
                new InitialTemperatureCalculator<>(
                        objectiveFunction,
                        neighborhood,
                        parameters
                );

        double percentage =
                calculator.calculateAcceptedPercentage(
                        10.0,
                        0.5,
                        new Random(123L)
                );

        assertEquals(
                0.0,
                percentage,
                1e-7
        );
    }

    @Test
    void acceptedPercentageShouldBeOneWhenTemperatureIsEnough() {

        Neighborhood<Double> neighborhood =
                (solution, random) ->
                        solution + 1.0;

        InitialTemperatureParameters parameters =
                new InitialTemperatureParameters(
                        1.0,
                        0.9,
                        0.01,
                        100,
                        100
                );

        InitialTemperatureCalculator<Double> calculator =
                new InitialTemperatureCalculator<>(
                        objectiveFunction,
                        neighborhood,
                        parameters
                );

        double percentage =
                calculator.calculateAcceptedPercentage(
                        10.0,
                        1.0,
                        new Random(123L)
                );

        assertEquals(
                1.0,
                percentage,
                1e-7
        );
    }

    @Test
    void calculateShouldFindThresholdNearOne() {

        /*
         * Con este problema:
         *
         * T < 1  -> 0% aceptación
         * T >= 1 -> 100% aceptación
         *
         * No existe una T con aceptación exactamente 90%.
         * La búsqueda binaria debe converger alrededor
         * del punto de transición T = 1.
         */
        Neighborhood<Double> neighborhood =
                (solution, random) ->
                        solution + 1.0;

        InitialTemperatureParameters parameters =
                new InitialTemperatureParameters(
                        0.25,
                        0.9,
                        0.001,
                        100,
                        100
                );

        InitialTemperatureCalculator<Double> calculator =
                new InitialTemperatureCalculator<>(
                        objectiveFunction,
                        neighborhood,
                        parameters
                );

        double temperature =
                calculator.calculate(
                        10.0,
                        123L
                );

        assertEquals(
                1.0,
                temperature,
                0.01
        );
    }

    @Test
    void calculateShouldBeReproducibleWithSameSeed() {

        Neighborhood<Double> neighborhood =
                (solution, random) -> {

                    double movement =
                            random.nextDouble(
                                    0.0,
                                    1.0
                            );

                    return solution + movement;
                };

        InitialTemperatureParameters parameters =
                new InitialTemperatureParameters(
                        0.5,
                        0.9,
                        0.01,
                        1000,
                        100
                );

        InitialTemperatureCalculator<Double> calculator =
                new InitialTemperatureCalculator<>(
                        objectiveFunction,
                        neighborhood,
                        parameters
                );

        double first =
                calculator.calculate(
                        10.0,
                        987654321L
                );

        double second =
                calculator.calculate(
                        10.0,
                        987654321L
                );

        assertEquals(
                first,
                second,
                1e-7
        );
    }
}