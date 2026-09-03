package mx.unam.heuristicas.tsp;

import mx.unam.heuristicas.config.DatabaseConfig;
import mx.unam.heuristicas.config.DatabaseConnection;
import mx.unam.heuristicas.dao.CityDAO;
import mx.unam.heuristicas.dao.ConnectionDAO;
import mx.unam.heuristicas.dao.jdbc.JdbcCityDAO;
import mx.unam.heuristicas.dao.jdbc.JdbcConnectionDAO;
import mx.unam.heuristicas.model.City;
import mx.unam.heuristicas.model.Connection;
import mx.unam.heuristicas.util.DoublePrecision;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TspInstanceFactoryTest {

        private CityDAO cityDAO;
        private ConnectionDAO connectionDAO;

        private TspInstanceFactory factory;

        @BeforeEach
        void setUp() {

                DatabaseConnection databaseConnection = new DatabaseConnection(
                                new DatabaseConfig("application-test.properties"));

                cityDAO = new JdbcCityDAO(
                                databaseConnection);

                connectionDAO = new JdbcConnectionDAO(
                                databaseConnection);

                factory = new TspInstanceFactory(
                                cityDAO,
                                connectionDAO);
        }

        @Test
        void naturalDistanceShouldBeZeroForSameCity() {

                City city = cityDAO.findById(1)
                                .orElseThrow();

                double distance = TspInstanceFactory
                                .naturalDistance(
                                                city,
                                                city);

                System.out.println("Distancias entre misma ciudadad: " + distance);

                assertEquals(
                                0.0,
                                distance,
                                DoublePrecision.TOLERANCE);
        }

        @Test
        void naturalDistanceShouldBeSymmetric() {

                List<City> cities = cityDAO.findCitiesByIds(
                                new int[] { 1, 7 });

                assertEquals(
                                2,
                                cities.size());

                City first = cities.get(0);

                City second = cities.get(1);

                double firstToSecond = TspInstanceFactory
                                .naturalDistance(
                                                first,
                                                second);

                double secondToFirst = TspInstanceFactory
                                .naturalDistance(
                                                second,
                                                first);

                System.out.println("Distancia de " + first.getName() + " a " + second.getName() + ": " + firstToSecond);
                System.out.println("Distancia de " + second.getName() + " a " + first.getName() + ": " + secondToFirst);

                assertEquals(
                                firstToSecond,
                                secondToFirst,
                                DoublePrecision.TOLERANCE);
        }

        @Test
        void naturalDistanceShouldMatchDatabaseDistance() {

                int[] cityIds = {
                                2,
                                7
                };

                List<City> cities = cityDAO.findCitiesByIds(
                                cityIds);

                List<Connection> connections = connectionDAO
                                .findConnectionsBetweenCities(
                                                cityIds);

                assertEquals(
                                2,
                                cities.size());

                assertFalse(
                                connections.isEmpty());

                City first = cities.get(0);

                City second = cities.get(1);

                Connection connection = connections.get(0);

                double calculatedDistance = TspInstanceFactory
                                .naturalDistance(
                                                first,
                                                second);
                System.out.println("Distancia calculada: " + calculatedDistance);
                System.out.println("Distancia de la base de datos: " + connection.getDistance());

                assertEquals(
                                connection.getDistance(),
                                calculatedDistance,
                                0.01);
        }

        @Test
        void calculateMaxDistanceShouldReturnLargestDistance() {

                List<Connection> connections = connectionDAO
                                .findConnectionsBetweenCities(
                                                new int[] {
                                                                1,
                                                                2,
                                                                3,
                                                                4,
                                                                5,
                                                                6,
                                                                7
                                                });

                assertFalse(
                                connections.isEmpty());

                double expected = connections.stream()
                                .mapToDouble(
                                                Connection::getDistance)
                                .max()
                                .orElseThrow();

                double result = TspInstanceFactory
                                .calculateMaxDistance(
                                                connections);

                System.out.println("Distancia máxima esperada: " + expected);
                System.out.println("Distancia máxima calculada: " + result);

                assertEquals(
                                expected,
                                result,
                                DoublePrecision.TOLERANCE);
        }

        @Test
        void calculateNormalizerShouldSumLargestNMinusOneDistances() {

                int[] cityIds = {
                                1,
                                2,
                                3,
                                4,
                                5,
                                6,
                                7
                };

                List<Connection> connections = connectionDAO
                                .findConnectionsBetweenCities(
                                                cityIds);

                assertFalse(
                                connections.isEmpty());

                double expected = connections.stream()
                                .mapToDouble(
                                                Connection::getDistance)
                                .boxed()
                                .sorted(
                                                java.util.Comparator
                                                                .reverseOrder())
                                .limit(
                                                cityIds.length - 1L)
                                .mapToDouble(
                                                Double::doubleValue)
                                .sum();

                double result = TspInstanceFactory
                                .calculateNormalizer(
                                                connections,
                                                cityIds.length);

                System.out.println("Normalizador esperado: " + expected);
                System.out.println("Normalizador calculado: " + result);

                assertEquals(
                                expected,
                                result,
                                DoublePrecision.TOLERANCE);
        }

        @Test
        void createShouldPreserveCityIdOrder() {

                int[] cityIds = {
                                7,
                                1,
                                4,
                                2
                };

                TspInstance instance = factory.create(
                                cityIds);

                assertEquals(
                                cityIds.length,
                                instance.size());

                assertEquals(
                                7,
                                instance.getCityId(0));

                assertEquals(
                                1,
                                instance.getCityId(1));

                assertEquals(
                                4,
                                instance.getCityId(2));

                assertEquals(
                                2,
                                instance.getCityId(3));
        }

        @Test
        void createShouldProduceSymmetricWeightMatrix() {

                int[] cityIds = {
                                7,
                                1,
                                4,
                                2
                };

                TspInstance instance = factory.create(
                                cityIds);

                for (int i = 0; i < instance.size(); i++) {

                        for (int j = 0; j < instance.size(); j++) {

                                assertEquals(
                                                instance.getWeight(i, j),
                                                instance.getWeight(j, i),
                                                DoublePrecision.TOLERANCE);
                        }
                }
        }

        @Test
        void createShouldHaveZeroDiagonal() {

                TspInstance instance = factory.create(
                                new int[] {
                                                7,
                                                1,
                                                4,
                                                2
                                });

                for (int i = 0; i < instance.size(); i++) {

                        assertEquals(
                                        0.0,
                                        instance.getWeight(i, i),
                                        DoublePrecision.TOLERANCE);
                }
        }

        @Test
        void createShouldProduceFinitePositiveWeights() {

                TspInstance instance = factory.create(
                                new int[] {
                                                7,
                                                1,
                                                2,
                                                3,
                                                4
                                });

                for (int i = 0; i < instance.size(); i++) {

                        for (int j = i + 1; j < instance.size(); j++) {

                                double weight = instance.getWeight(
                                                i,
                                                j);

                                assertTrue(
                                                Double.isFinite(weight));

                                assertTrue(
                                                weight > 0.0);
                        }
                }
                // quiero imprimir la matriz weights
                System.out.println("Matriz de pesos:");
                for (int i = 0; i < instance.size(); i++) {
                        for (int j = 0; j < instance.size(); j++) {
                                System.out.print(instance.getWeight(i, j) + "\t");
                        }
                        System.out.println();
                }
        }

        @Test
        void createShouldProducePositiveNormalizer() {

                TspInstance instance = factory.create(
                                new int[] {
                                                7,
                                                1,
                                                2,
                                                3,
                                                4
                                });

                System.out.println("Normalizador calculado: " + instance.getNormalizer());

                assertTrue(
                                instance.getNormalizer() > 0.0);

                assertTrue(
                                Double.isFinite(
                                                instance.getNormalizer()));
        }
}