package mx.unam.heuristicas.dao;

import mx.unam.heuristicas.config.DatabaseConfig;
import mx.unam.heuristicas.config.DatabaseConnection;
import mx.unam.heuristicas.dao.jdbc.JdbcCityDAO;
import mx.unam.heuristicas.model.City;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JdbcCityDAOTest {

        private CityDAO cityDAO;

        @BeforeEach
        void setUp() {

                DatabaseConfig config = new DatabaseConfig(
                                "application-test.properties");

                DatabaseConnection database = new DatabaseConnection(config);

                cityDAO = new JdbcCityDAO(database);
        }

        @Test
        void findByIdShouldReturnExistingCity()
                        throws Exception {

                Optional<City> result = cityDAO.findById(1);

                assertTrue(result.isPresent());

                City city = result.get();

                assertEquals(1, city.getId());
        }

        @Test
        void findByIdShouldReturnEmptyForUnknownCity()
                        throws Exception {

                Optional<City> result = cityDAO.findById(-999);

                assertTrue(result.isEmpty());
        }

        @Test
        void findByIdShouldReturnCityWhenCityExists() throws Exception {

                Optional<City> result = cityDAO.findById(1);

                assertTrue(result.isPresent());

                City city = result.get();

                assertEquals(1, city.getId());
                assertEquals("Tokyo", city.getName());
                assertEquals("Japan", city.getCountry());
                assertEquals(31480498, city.getPopulation());

                assertEquals(
                                35.685,
                                city.getLatitude(),
                                0.000001);

                assertEquals(
                                139.751,
                                city.getLongitude(),
                                0.000001);
        }

        @Test
        void findAllShouldReturnAllCitys() throws Exception {

                List<City> result = cityDAO.findAll();

                assertFalse(result.isEmpty());

                assertEquals(1092, result.size());
        }

        @Test
        void findCitiesByIdsShouldReturnRequestedCitiesOrderedById() {

                int[] cityIds = { 4, 1, 3, 2 };

                List<City> cities = cityDAO.findCitiesByIds(cityIds);

                assertNotNull(cities);
                assertEquals(4, cities.size());

                assertEquals(1, cities.get(0).getId());
                assertEquals(2, cities.get(1).getId());
                assertEquals(3, cities.get(2).getId());
                assertEquals(4, cities.get(3).getId());
        }
}