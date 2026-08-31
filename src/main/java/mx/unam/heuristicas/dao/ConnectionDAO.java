package mx.unam.heuristicas.dao;

import mx.unam.heuristicas.model.Connection;

import java.sql.SQLException;
import java.util.List;

public interface ConnectionDAO {

        List<Connection> findConnectionsBetweenCities(
                        int[] cityIds)
                        throws SQLException;
}