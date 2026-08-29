package mx.unam.heuristicas.dao;

import mx.unam.heuristicas.model.Connection;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ConnectionDAO {

    Optional<Connection> find(int city1, int city2)
            throws SQLException;

    List<Connection> findByCityIds(List<Integer> cityIds)
            throws SQLException;
}