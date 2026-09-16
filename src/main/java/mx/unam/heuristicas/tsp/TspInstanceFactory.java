package mx.unam.heuristicas.tsp;

import mx.unam.heuristicas.dao.CityDAO;
import mx.unam.heuristicas.dao.ConnectionDAO;
import mx.unam.heuristicas.exception.AppException;
import mx.unam.heuristicas.model.City;
import mx.unam.heuristicas.model.Connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class TspInstanceFactory {

    private static final double EARTH_RADIUS_METERS = 6_373_000.0;

    private final CityDAO cityDAO;
    private final ConnectionDAO connectionDAO;

    public TspInstanceFactory(CityDAO cityDAO, ConnectionDAO connectionDAO) {
        this.cityDAO =
                Objects.requireNonNull(
                        cityDAO,
                        "CityDAO no puede ser null"
                );

        this.connectionDAO =
                Objects.requireNonNull(
                        connectionDAO,
                        "ConnectionDAO no puede ser null"
                );
    }

    public TspInstance create(int[] cityIds) {

        Objects.requireNonNull(
                cityIds,
                "Los IDs de las ciudades no pueden ser null"
        );

        List<City> cities =
                cityDAO.findCitiesByIds(cityIds);

        validateCitiesExist(
                cityIds.clone(),
                cities
        );

        List<Connection> connections =
                connectionDAO.findConnectionsBetweenCities(
                        cityIds
                );

        if (connections.isEmpty()) {
            throw new AppException(
                    "La instancia no contiene conexiones reales"
            );
        }

        Map<Integer, Integer> indexByCityId =
                createIndexByCityId(cityIds);

        Map<Integer, City> cityById =
                createCityById(cities);

        double[][] weights =
                createEmptyWeights(cityIds.length);

        fillRealWeights(
                weights,
                connections,
                indexByCityId
        );

        double maxDistance =
                calculateMaxDistance(connections);

        fillMissingWeights(
                weights,
                cityIds,
                cityById,
                maxDistance
        );

        double normalizer =
                calculateNormalizer(
                        connections,
                        cityIds.length
                );

        return new TspInstance(
                cityIds,
                weights,
                normalizer
        );
    }

    private static void validateCitiesExist(int[] cityIds, List<City> cities) {

        Objects.requireNonNull(
                cities,
                "CityDAO devolvió null"
        );

        if (cities.size() != cityIds.length) {

            throw new AppException(
                    "Una o más ciudades de la instancia "
                    + "no existen en la base de datos"
            );
        }

        Arrays.sort(cityIds);

        int i = 0;
        for (City city : cities) {
            if (city == null) {
                throw new AppException(
                        "CityDAO devolvió una ciudad null"
                );
            }

            if (city.getId() != cityIds[i]) {
                throw new AppException(
                        "CityDAO devolvió una ciudad con "
                        + "ID diferente al solicitado"
                );
            }
            i++;
        }
    }

    private static Map<Integer, Integer>
    createIndexByCityId(int[] cityIds) {

        Map<Integer, Integer> indexByCityId =
                new HashMap<>();

        for (int i = 0; i < cityIds.length; i++) {

            indexByCityId.put(
                    cityIds[i],
                    i
            );
        }

        return indexByCityId;
    }

    private static Map<Integer, City>
    createCityById(List<City> cities) {

        Map<Integer, City> cityById =
                new HashMap<>();

        for (City city : cities) {

            cityById.put(
                    city.getId(),
                    city
            );
        }

        return cityById;
    }

    private static double[][] createEmptyWeights(int size) {

        double[][] weights =
                new double[size][size];

        for (int i = 0; i < size; i++) {

            Arrays.fill(
                    weights[i],
                    Double.NaN
            );

            weights[i][i] = 0.0;
        }

        return weights;
    }

    private static void fillRealWeights(
            double[][] weights,
            List<Connection> connections,
            Map<Integer, Integer> indexByCityId
    ) {

        for (Connection connection : connections) {

            Integer fromIndex =
                    indexByCityId.get(
                            connection.getIdCity1()
                    );

            Integer toIndex =
                    indexByCityId.get(
                            connection.getIdCity2()
                    );

            if (fromIndex == null || toIndex == null) {
                throw new IllegalStateException(
                        "La conexión contiene una ciudad "
                        + "que no pertenece a la instancia"
                );
            }

            double distance =
                    connection.getDistance();

            if (!Double.isFinite(distance)
                    || distance < 0.0) {

                throw new IllegalArgumentException(
                        "Distancia inválida en la conexión: "
                        + distance
                );
            }

            weights[fromIndex][toIndex] =
                    distance;

            weights[toIndex][fromIndex] =
                    distance;
        }
    }

    static double calculateMaxDistance(
            List<Connection> connections
    ) {

        if (connections.isEmpty()) {
            throw new IllegalArgumentException(
                    "No existen conexiones para calcular "
                    + "la distancia máxima"
            );
        }

        double maxDistance =
                Double.NEGATIVE_INFINITY;

        for (Connection connection : connections) {

            double distance =
                    connection.getDistance();

            if (distance > maxDistance) {
                maxDistance = distance;
            }
        }

        return maxDistance;
    }

    private static void fillMissingWeights(
            double[][] weights,
            int[] cityIds,
            Map<Integer, City> cityById,
            double maxDistance
    ) {

        for (int i = 0; i < weights.length; i++) {

            for (int j = i + 1; j < weights.length; j++) {

                if (!Double.isNaN(
                        weights[i][j]
                )) {
                    continue;
                }

                City from =
                        cityById.get(
                                cityIds[i]
                        );

                City to =
                        cityById.get(
                                cityIds[j]
                        );

                if (from == null || to == null) {
                    throw new IllegalStateException(
                            "No se encontró información "
                            + "de una ciudad"
                    );
                }

                double distance =
                        naturalDistance(
                                from,
                                to
                        );

                double augmentedWeight =
                        distance * maxDistance;

                weights[i][j] =
                        augmentedWeight;

                weights[j][i] =
                        augmentedWeight;
            }
        }
    }

    static double naturalDistance(
            City from,
            City to
    ) {

        Objects.requireNonNull(
                from,
                "La ciudad origen no puede ser null"
        );

        Objects.requireNonNull(
                to,
                "La ciudad destino no puede ser null"
        );

        double latitudeFrom =
                Math.toRadians(
                        from.getLatitude()
                );

        double longitudeFrom =
                Math.toRadians(
                        from.getLongitude()
                );

        double latitudeTo =
                Math.toRadians(
                        to.getLatitude()
                );

        double longitudeTo =
                Math.toRadians(
                        to.getLongitude()
                );

        double latitudeDifference =
                latitudeTo - latitudeFrom;

        double longitudeDifference =
                longitudeTo - longitudeFrom;

        double sinLatitude =
                Math.sin(
                        latitudeDifference / 2.0
                );

        double sinLongitude =
                Math.sin(
                        longitudeDifference / 2.0
                );

        double a =
                sinLatitude * sinLatitude
                +
                Math.cos(latitudeFrom)
                * Math.cos(latitudeTo)
                * sinLongitude
                * sinLongitude;

        /*
         * Evita pequeños errores de punto flotante
         * que pudieran producir valores ligeramente
         * menores que 0 o mayores que 1.
         */
        a = Math.max(
                0.0,
                Math.min(1.0, a)
        );

        double c =
                2.0
                * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1.0 - a)
                );

        return EARTH_RADIUS_METERS * c;
    }

    static double calculateNormalizer(
            List<Connection> connections,
            int numberOfCities
    ) {

        if (numberOfCities < 2) {
            throw new IllegalArgumentException(
                    "Se requieren al menos dos ciudades"
            );
        }

        if (connections.isEmpty()) {
            throw new IllegalArgumentException(
                    "No existen conexiones reales para "
                    + "calcular el normalizador"
            );
        }

        List<Double> distances =
                new ArrayList<>(
                        connections.size()
                );

        for (Connection connection : connections) {
            distances.add(
                    connection.getDistance()
            );
        }

        distances.sort(
                Comparator.reverseOrder()
        );

        int numberOfDistances =
                Math.min(
                        numberOfCities - 1,
                        distances.size()
                );

        double normalizer = 0.0;

        for (int i = 0; i < numberOfDistances; i++) {

            normalizer +=
                    distances.get(i);
        }

        if (!Double.isFinite(normalizer)
                || normalizer <= 0.0) {

            throw new IllegalArgumentException(
                    "El normalizador debe ser "
                    + "positivo y finito"
            );
        }

        return normalizer;
    }
}