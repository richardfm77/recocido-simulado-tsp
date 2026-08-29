package mx.unam.heuristicas.dao;

import mx.unam.heuristicas.model.City;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CityDAO {
    // Funciones Genericas
    Optional<City> findById(int id) throws SQLException;

    List<City> findAll() throws SQLException;
}