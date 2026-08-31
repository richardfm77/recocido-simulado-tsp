package mx.unam.heuristicas.dao.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mx.unam.heuristicas.config.DatabaseConnection;
import mx.unam.heuristicas.dao.ConnectionDAO;
import mx.unam.heuristicas.model.Connection;
import mx.unam.heuristicas.util.HelperBD;

public final class JdbcConnectionDAO implements ConnectionDAO {

    private final DatabaseConnection databaseConnection;

    public JdbcConnectionDAO(
            DatabaseConnection databaseConnection) {
        this.databaseConnection = Objects.requireNonNull(
                databaseConnection);
    }

    @Override
    public List<Connection> findConnectionsBetweenCities(int[] cityIds) {

        Objects.requireNonNull(
                cityIds,
                "Los IDs no pueden ser null");

        if (cityIds.length == 0) {
            return List.of();
        }

        String placeholders = HelperBD.createPlaceholders(
                cityIds.length);

        String sql = """
                SELECT id_city_1,
                       id_city_2,
                       distance
                FROM connections
                WHERE id_city_1 IN (%s)
                  AND id_city_2 IN (%s)
                """.formatted(
                placeholders,
                placeholders);

        List<Connection> connections = new ArrayList<>();

        try (
                java.sql.Connection connectionDB = databaseConnection.getConnection();

                PreparedStatement statement = connectionDB.prepareStatement(sql)) {

            int parameterIndex = 1;

            for (int cityId : cityIds) {
                statement.setInt(
                        parameterIndex++,
                        cityId);
            }

            for (int cityId : cityIds) {
                statement.setInt(
                        parameterIndex++,
                        cityId);
            }

            try (
                    ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    connections.add(
                            new Connection(
                                    resultSet.getInt(
                                            "id_city_1"),
                                    resultSet.getInt(
                                            "id_city_2"),
                                    resultSet.getDouble(
                                            "distance")));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al consultar conexiones",
                    e);
        }

        return connections;
    }
}