package mx.unam.heuristicas.dao.jdbc;

import mx.unam.heuristicas.config.DatabaseConnection;
import mx.unam.heuristicas.dao.CityDAO;
import mx.unam.heuristicas.model.City;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCityDAO implements CityDAO {

    private final DatabaseConnection database;

    public JdbcCityDAO(DatabaseConnection database) {
        this.database = database;
    }

    @Override
    public Optional<City> findById(int id) throws SQLException {

        String sql = """
                SELECT
                    id,
                    name,
                    country,
                    population,
                    latitude,
                    longitude
                FROM cities
                WHERE id = ?
                """;

        try (
                Connection connection = database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapCity(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<City> findAll() throws SQLException {

        String sql = """
                SELECT
                    id,
                    name,
                    country,
                    population,
                    latitude,
                    longitude
                FROM cities
                """;

        List<City> cities = new ArrayList<>();

        try (
                Connection connection = database.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                cities.add(mapCity(resultSet));
            }
        }

        return cities;
    }

    private City mapCity(ResultSet resultSet) throws SQLException {

        return new City(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("country"),
                resultSet.getInt("population"),
                resultSet.getDouble("latitude"),
                resultSet.getDouble("longitude")
        );
    }
}