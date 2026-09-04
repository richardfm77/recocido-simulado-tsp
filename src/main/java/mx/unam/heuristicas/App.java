package mx.unam.heuristicas;

import mx.unam.heuristicas.config.DatabaseConfig;
import mx.unam.heuristicas.config.DatabaseConnection;
import mx.unam.heuristicas.dao.CityDAO;
import mx.unam.heuristicas.dao.ConnectionDAO;
import mx.unam.heuristicas.dao.jdbc.JdbcCityDAO;
import mx.unam.heuristicas.dao.jdbc.JdbcConnectionDAO;
import mx.unam.heuristicas.exception.AppException;
import mx.unam.heuristicas.heuristic.OptimizationResult;
import mx.unam.heuristicas.heuristic.ThresholdAccepting;
import mx.unam.heuristicas.heuristic.ThresholdAcceptingParameters;
import mx.unam.heuristicas.tsp.TspCostFunction;
import mx.unam.heuristicas.tsp.TspFileReader;
import mx.unam.heuristicas.tsp.TspInstance;
import mx.unam.heuristicas.tsp.TspInstanceFactory;
import mx.unam.heuristicas.tsp.TspNeighborhood;
import mx.unam.heuristicas.tsp.TspSolution;

import java.nio.file.Path;

public final class App {

    private static final long SEED = 123L;

    private App() {
    }

    public static void main(String[] args) {

        try {

            run(args);

        } catch (AppException e) {

            System.err.println(
                    "Error: " + e.getMessage());

           throw e;

        } catch (IllegalArgumentException e) {

            System.err.println(
                    "Argumento inválido: "
                            + e.getMessage());

            throw e;

        } catch (Exception e) {

            System.err.println(
                    "Ocurrió un error inesperado");

            e.printStackTrace();

            throw e;
        }
    }

    private static void run(String[] args) {

        if (args.length != 1) {
            throw new AppException(
                    "Uso: java -jar programa.jar archivo.tsp");
        }

        Path tspPath = Path.of(
                args[0]);

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

        OptimizationResult<TspSolution> result = heuristic.optimize(
                initialSolution,
                SEED);

        printResult(
                instance,
                result);
    }

    private static void printResult(
            TspInstance instance,
            OptimizationResult<TspSolution> result) {

        System.out.println(
                "Semilla: "
                        + result.seed());

        System.out.println(
                "Mejor costo: "
                        + result.bestCost());

        System.out.println(
                "Mejor solución:");

        TspSolution solution = result.bestSolution();

        for (int i = 0; i < solution.size(); i++) {

            int cityIndex = solution.get(i);

            int cityId = instance.getCityId(
                    cityIndex);

            if (i > 0) {
                System.out.print(",");
            }

            System.out.print(
                    cityId);
        }

        System.out.println();

        System.out.println(
                "Vecinos generados: "
                        + result.generatedNeighbors());

        System.out.println(
                "Vecinos aceptados: "
                        + result.acceptedNeighbors());

        System.out.println(
                "Niveles de temperatura: "
                        + result.temperatureLevels());
    }
}