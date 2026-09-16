package mx.unam.heuristicas.tsp;

import mx.unam.heuristicas.config.DatabaseConfig;
import mx.unam.heuristicas.config.DatabaseConnection;
import mx.unam.heuristicas.dao.CityDAO;
import mx.unam.heuristicas.dao.ConnectionDAO;
import mx.unam.heuristicas.dao.jdbc.JdbcCityDAO;
import mx.unam.heuristicas.dao.jdbc.JdbcConnectionDAO;
import mx.unam.heuristicas.heuristic.OptimizationResult;
import mx.unam.heuristicas.heuristic.ThresholdAccepting;
import mx.unam.heuristicas.heuristic.ThresholdAcceptingParameters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TspThresholdAcceptingIntegrationTest {

    private TspInstanceFactory instanceFactory;

    @BeforeEach
    void setUp() {

        DatabaseConnection databaseConnection = new DatabaseConnection(
                new DatabaseConfig("application-test.properties"));

        CityDAO cityDAO = new JdbcCityDAO(
                databaseConnection);

        ConnectionDAO connectionDAO = new JdbcConnectionDAO(
                databaseConnection);

        instanceFactory = new TspInstanceFactory(
                cityDAO,
                connectionDAO);
    }

    @Test
    void shouldOptimizeTspInstance() throws URISyntaxException {

        Path path = getResourcePath("input-40.tsp");

        int[] cityIds = TspFileReader.read(path);

        TspInstance instance = instanceFactory.create(
                cityIds);

        TspSolution initialSolution = createInitialSolution(
                instance.size());

        TspCostFunction costFunction = new TspCostFunction(
                instance);

        TspNeighborhood neighborhood = new TspNeighborhood();

        ThresholdAcceptingParameters parameters = new ThresholdAcceptingParameters(
                0.1,
                1e-7,
                0.9,
                20,
                200);

        ThresholdAccepting<TspSolution> heuristic = new ThresholdAccepting<>(
                costFunction,
                neighborhood,
                parameters);

        double initialCost = costFunction.evaluate(
                initialSolution);

        OptimizationResult<TspSolution> result = heuristic.optimize(
                initialSolution,
                123L);

        assertNotNull(
                result);

        assertNotNull(
                result.bestSolution());

        assertTrue(
                Double.isFinite(
                        result.bestCost()));

        assertTrue(
                result.bestCost() >= 0.0);

        assertTrue(
                result.bestCost() <= initialCost);

        assertEquals(
                initialSolution.size(),
                result.bestSolution().size());

        assertSameCities(
                initialSolution,
                result.bestSolution());
    }

    private static TspSolution createInitialSolution(
            int size) {

        int[] permutation = new int[size];

        for (int i = 0; i < size; i++) {
            permutation[i] = i;
        }

        return new TspSolution(
                permutation);
    }

    private static void assertSameCities(
            TspSolution expected,
            TspSolution actual) {

        int[] expectedPermutation = expected.toArray();

        int[] actualPermutation = actual.toArray();

        Arrays.sort(
                expectedPermutation);

        Arrays.sort(
                actualPermutation);

        assertArrayEquals(
                expectedPermutation,
                actualPermutation);
    }

    private Path getResourcePath(String fileName)
            throws URISyntaxException {

        return Path.of(
                getClass()
                        .getResource("/" + fileName)
                        .toURI());
    }
}