package mx.unam.heuristicas.report;

import mx.unam.heuristicas.exception.AppException;
import mx.unam.heuristicas.model.City;
import mx.unam.heuristicas.tsp.TspInstance;
import mx.unam.heuristicas.tsp.TspSolution;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class TspRouteMapWriter {

    private static final String FILE_NAME = "mapa-ruta.html";

    private final Path reportDirectory;

    public TspRouteMapWriter(Path reportDirectory) {
        this.reportDirectory = Objects.requireNonNull(
                reportDirectory,
                "El directorio de reportes no puede ser null"
        );
    }

    public void write(
            TspInstance instance,
            TspSolution solution,
            List<City> cities,
            double cost
    ) {
        Objects.requireNonNull(instance, "La instancia no puede ser null");
        Objects.requireNonNull(solution, "La solución no puede ser null");
        Objects.requireNonNull(cities, "Las ciudades no pueden ser null");

        Map<Integer, City> cityById = createCityMap(cities);

        String html = createHtml(
                instance,
                solution,
                cityById,
                cost
        );

        try {
            Files.createDirectories(reportDirectory);

            Path output = reportDirectory.resolve(FILE_NAME);

            Files.writeString(
                    output,
                    html,
                    StandardCharsets.UTF_8
            );

            System.out.println(
                    "Mapa de ruta generado: "
                            + output.toAbsolutePath()
            );

        } catch (IOException e) {
            throw new AppException(
                    "No se pudo generar el mapa de la ruta",
                    e
            );
        }
    }

    private static Map<Integer, City> createCityMap(
            List<City> cities
    ) {
        Map<Integer, City> cityById = new HashMap<>();

        for (City city : cities) {
            cityById.put(
                    city.getId(),
                    city
            );
        }

        return cityById;
    }

    private static String createHtml(
            TspInstance instance,
            TspSolution solution,
            Map<Integer, City> cityById,
            double cost
    ) {
        StringBuilder markers = new StringBuilder();
        StringBuilder route = new StringBuilder();

        for (int i = 0; i < solution.size(); i++) {

            int internalIndex = solution.get(i);
            int cityId = instance.getCityId(internalIndex);

            City city = cityById.get(cityId);

            if (city == null) {
                throw new AppException(
                        "No se encontró información de la ciudad con ID "
                                + cityId
                );
            }

            double latitude = city.getLatitude();
            double longitude = city.getLongitude();

            if (!Double.isFinite(latitude)
                    || !Double.isFinite(longitude)) {
                throw new AppException(
                        "Coordenadas inválidas para la ciudad "
                                + cityId
                );
            }

            markers.append("""
                    L.marker([%f, %f])
                        .addTo(map)
                        .bindPopup(
                            "<b>%d. %s</b><br>" +
                            "ID: %d<br>" +
                            "País: %s<br>" +
                            "Latitud: %f<br>" +
                            "Longitud: %f"
                        );
                    """.formatted(
                    latitude,
                    longitude,
                    i + 1,
                    escapeJavaScript(city.getName()),
                    cityId,
                    escapeJavaScript(city.getCountry()),
                    latitude,
                    longitude
            ));

            if (!route.isEmpty()) {
                route.append(",\n");
            }

            route.append(
                    "[%f, %f]".formatted(
                            latitude,
                            longitude
                    )
            );
        }

        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport"
                          content="width=device-width, initial-scale=1.0">

                    <title>Ruta TSP</title>

                    <link
                        rel="stylesheet"
                        href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
                    />

                    <style>
                        html, body {
                            margin: 0;
                            padding: 0;
                            height: 100%%;
                            font-family: Arial, sans-serif;
                        }

                        #map {
                            height: 100%%;
                            width: 100%%;
                        }

                        .info {
                            position: absolute;
                            z-index: 1000;
                            top: 15px;
                            right: 15px;
                            background: white;
                            padding: 12px 16px;
                            border-radius: 8px;
                            box-shadow: 0 2px 8px rgba(0,0,0,0.25);
                        }

                        .info h3 {
                            margin: 0 0 8px 0;
                        }

                        .info p {
                            margin: 4px 0;
                        }
                    </style>
                </head>

                <body>

                    <div class="info">
                        <h3>Ruta TSP</h3>
                        <p>Ciudades: %d</p>
                        <p>Mejor costo: %.12f</p>
                    </div>

                    <div id="map"></div>

                    <script
                        src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js">
                    </script>

                    <script>
                        const map = L.map('map');

                        L.tileLayer(
                            'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
                            {
                                maxZoom: 19,
                                attribution:
                                    '&copy; OpenStreetMap contributors'
                            }
                        ).addTo(map);

                        %s

                        const route = [
                            %s
                        ];

                        const routeLine = L.polyline(
                            route,
                            {
                                weight: 3,
                                opacity: 0.85
                            }
                        ).addTo(map);

                        map.fitBounds(
                            routeLine.getBounds(),
                            {
                                padding: [30, 30]
                            }
                        );
                    </script>

                </body>
                </html>
                """.formatted(
                solution.size(),
                cost,
                markers,
                route
        );
    }

    private static String escapeJavaScript(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("'", "\\'")
                .replace("\n", " ")
                .replace("\r", " ");
    }
}