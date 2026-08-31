package mx.unam.heuristicas.dao;

import mx.unam.heuristicas.config.DatabaseConfig;
import mx.unam.heuristicas.config.DatabaseConnection;
import mx.unam.heuristicas.dao.jdbc.JdbcConnectionDAO;
import mx.unam.heuristicas.model.Connection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JdbcConnectionDAOTest {

    private ConnectionDAO connectionDAO;

    @BeforeEach
    void setUp() {
         DatabaseConfig config = new DatabaseConfig(
                                "application-test.properties");

                DatabaseConnection database = new DatabaseConnection(config);

                connectionDAO = new JdbcConnectionDAO(database);
    }

    @Test
    void findConnectionsBetweenCitiesShouldReturnConnections() throws SQLException {

        int[] cityIds = {817,820};
        Set<Integer> expectedCityIds = new HashSet<>();
        expectedCityIds.add(817);
        expectedCityIds.add(820);   


        List<Connection> connections =
                connectionDAO.findConnectionsBetweenCities(
                        cityIds
                );

        assertNotNull(connections);
        assertFalse(connections.isEmpty());

        for (Connection connection : connections) {

            assertTrue(
                    expectedCityIds.contains(connection.getIdCity1())
            );

            assertTrue(
                    expectedCityIds.contains(connection.getIdCity2())
            );

            assertTrue(connection.getDistance() >= 0.0);
        }
    }

    @Test
    void findConnectionsBetweenCitiesShouldReturnEmptyListForUnknownCities() throws SQLException {

        int[] cityIds = {1,2};

        List<Connection> connections =
                connectionDAO.findConnectionsBetweenCities(
                        cityIds
                );

        assertNotNull(connections);
        assertTrue(connections.isEmpty());
    }

    @Test
    void findConnectionsBetweenCitiesShouldReturnEmptyListForEmptyInput() throws SQLException {

        int[] cityIds = {};

        List<Connection> connections =
                connectionDAO.findConnectionsBetweenCities(
                        cityIds
                );

        assertNotNull(connections);
        assertTrue(connections.isEmpty());
    }
}