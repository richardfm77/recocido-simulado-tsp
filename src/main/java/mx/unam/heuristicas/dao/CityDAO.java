package mx.unam.heuristicas.dao;

import mx.unam.heuristicas.exception.AppException;
import mx.unam.heuristicas.model.City;

import java.util.List;
import java.util.Optional;

public interface CityDAO {

    Optional<City> findById(int id) throws AppException;

    List<City> findAll() throws AppException;

    List<City> findCitiesByIds(int[] cityIds) throws AppException;
}