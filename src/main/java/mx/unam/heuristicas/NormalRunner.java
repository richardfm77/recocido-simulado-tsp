package mx.unam.heuristicas;

import java.nio.file.Path;

import mx.unam.heuristicas.config.DatabaseConfig;
import mx.unam.heuristicas.config.DatabaseConnection;
import mx.unam.heuristicas.config.HeuristicConfig;
import mx.unam.heuristicas.dao.CityDAO;
import mx.unam.heuristicas.dao.ConnectionDAO;
import mx.unam.heuristicas.dao.jdbc.JdbcCityDAO;
import mx.unam.heuristicas.dao.jdbc.JdbcConnectionDAO;
import mx.unam.heuristicas.heuristic.OptimizationResult;
import mx.unam.heuristicas.heuristic.ThresholdAccepting;
import mx.unam.heuristicas.heuristic.ThresholdAcceptingParameters;
import mx.unam.heuristicas.tsp.TspCostFunction;
import mx.unam.heuristicas.tsp.TspFileReader;
import mx.unam.heuristicas.tsp.TspInstance;
import mx.unam.heuristicas.tsp.TspInstanceFactory;
import mx.unam.heuristicas.tsp.TspNeighborhood;
import mx.unam.heuristicas.tsp.TspSolution;

public class NormalRunner {
    public static void runNormal(Path tspPath) {
        int[] cityIds = TspFileReader.read(
                tspPath);

        DatabaseConnection databaseConnection = new DatabaseConnection(
                new DatabaseConfig("application.properties"));

        CityDAO cityDAO = new JdbcCityDAO(
                databaseConnection);

        ConnectionDAO connectionDAO = new JdbcConnectionDAO(
                databaseConnection);

        TspInstanceFactory factory = new TspInstanceFactory(
                cityDAO,
                connectionDAO);

        TspInstance instance = factory.create(
                cityIds);

        TspSolution initialSolution = TspSolution.initial(
                instance.size());

        TspCostFunction costFunction = new TspCostFunction(
                instance);

        TspNeighborhood neighborhood = new TspNeighborhood();

        HeuristicConfig heuristicConfig = new HeuristicConfig("heuristic.properties");

        ThresholdAcceptingParameters parameters = heuristicConfig.getParameters();

        ThresholdAccepting<TspSolution> heuristic = new ThresholdAccepting<>(
                costFunction,
                neighborhood,
                parameters);

        double initialCost = costFunction.evaluate(initialSolution);

        long startTime = System.nanoTime();
        OptimizationResult<TspSolution> result = heuristic.optimize(
                initialSolution,
                heuristicConfig.getSeed());
        long endTime = System.nanoTime();

        long elapsedNanos = endTime - startTime;

        printResult(
                instance,
                initialSolution,
                initialCost,
                result,
                elapsedNanos);
    }

    private static void printResult(
            TspInstance instance,
            TspSolution initialSolution,
            double initialCost,
            OptimizationResult<TspSolution> result,
            long elapsedNanos) {

        double elapsedMilliseconds = elapsedNanos / 1_000_000.0;

        double elapsedSeconds = elapsedNanos / 1_000_000_000.0;

        System.out.println();
        System.out.println(
                "========== RESULTADO ==========");

        System.out.println(
                "Semilla: "
                        + result.seed());
        System.out.println(
                "Normalizador: "
                        + instance.getNormalizer());

        System.out.println(
                "Costo inicial: "
                        + initialCost);

        System.out.println(
                "Mejor costo: "
                        + result.bestCost());

        System.out.println(
                "Costo final: "
                        + result.finalCost());

        System.out.println(
                "Solución inicial: "
                        + toRealCityIds(
                                instance,
                                initialSolution));

        System.out.println(
                "Mejor solución: "
                        + toRealCityIds(
                                instance,
                                result.bestSolution()));

        System.out.println(
                "Solución final: "
                        + toRealCityIds(
                                instance,
                                result.finalSolution()));

        System.out.println(
                "Vecinos generados: "
                        + result.generatedNeighbors());

        System.out.println(
                "Vecinos aceptados: "
                        + result.acceptedNeighbors());

        System.out.println(
                "Niveles de temperatura: "
                        + result.temperatureLevels());

        System.out.printf(
                "Tiempo total: %.3f ms (%.6f s)%n",
                elapsedMilliseconds,
                elapsedSeconds);

        System.out.println(
                "===============================");
    }

    private static String toRealCityIds(
            TspInstance instance,
            TspSolution solution) {

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < solution.size(); i++) {

            if (i > 0) {
                builder.append(",");
            }

            int internalIndex = solution.get(i);

            int cityId = instance.getCityId(
                    internalIndex);

            builder.append(
                    cityId);
        }

        return builder.toString();
    }
}
