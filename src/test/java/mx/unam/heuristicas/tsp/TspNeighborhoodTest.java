package mx.unam.heuristicas.tsp;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TspNeighborhoodTest {

    private final TspNeighborhood neighborhood = new TspNeighborhood();

    @Test
    void shouldGenerateDifferentNeighbor() {

        TspSolution solution = new TspSolution(
                new int[] {
                        0,
                        1,
                        2,
                        3,
                        4
                });

        TspSolution neighbor = neighborhood.generateNeighbor(
                solution,
                new Random(123L));

        assertFalse(
                Arrays.equals(
                        solution.toArray(),
                        neighbor.toArray()));
    }

    @Test
    void shouldPreserveAllCities() {

        TspSolution solution = new TspSolution(
                new int[] {
                        0,
                        1,
                        2,
                        3,
                        4
                });

        TspSolution neighbor = neighborhood.generateNeighbor(
                solution,
                new Random(123L));

        int[] original = solution.toArray();

        int[] generated = neighbor.toArray();

        Arrays.sort(original);
        Arrays.sort(generated);

        assertArrayEquals(
                original,
                generated);
    }

    @Test
    void shouldChangeExactlyTwoPositions() {

        TspSolution solution = new TspSolution(
                new int[] {
                        0,
                        1,
                        2,
                        3,
                        4
                });

        TspSolution neighbor = neighborhood.generateNeighbor(
                solution,
                new Random(123L));

        int differentPositions = 0;

        for (int i = 0; i < solution.size(); i++) {

            if (solution.get(i) != neighbor.get(i)) {

                differentPositions++;
            }
        }

        assertEquals(
                2,
                differentPositions);
    }

    @Test
    void shouldNotModifyOriginalSolution() {

        TspSolution solution = new TspSolution(
                new int[] {
                        0,
                        1,
                        2,
                        3,
                        4
                });

        int[] original = solution.toArray();

        neighborhood.generateNeighbor(
                solution,
                new Random(123L));

        assertArrayEquals(
                original,
                solution.toArray());
    }

    @Test
    void shouldBeReproducibleWithSameSeed() {

        TspSolution solution = new TspSolution(
                new int[] {
                        0,
                        1,
                        2,
                        3,
                        4
                });

        TspSolution first = neighborhood.generateNeighbor(
                solution,
                new Random(123L));

        TspSolution second = neighborhood.generateNeighbor(
                solution,
                new Random(123L));

        assertArrayEquals(
                first.toArray(),
                second.toArray());
    }

    @Test
    void shouldWorkWithTwoCities() {

        TspSolution solution = new TspSolution(
                new int[] {
                        0,
                        1
                });

        TspSolution neighbor = neighborhood.generateNeighbor(
                solution,
                new Random(123L));

        assertArrayEquals(
                new int[] {
                        1,
                        0
                },
                neighbor.toArray());
    }
}