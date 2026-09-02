package mx.unam.heuristicas.tsp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public final class TspFileReader {

    private TspFileReader() {
    }

    public static int[] read(Path path) {

        if (path == null) {
            throw new IllegalArgumentException(
                    "La ruta del archivo no puede ser null"
            );
        }

        String content;

        try {
            content = Files.readString(path);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "No se pudo leer el archivo TSP: " + path,
                    e
            );
        }

        return parse(content);
    }

    private static int[] parse(String content) {

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException(
                    "El archivo TSP está vacío"
            );
        }

        String cleaned = content.trim();

        if (cleaned.endsWith(",")) {
            cleaned = cleaned.substring(
                    0,
                    cleaned.length() - 1
            );
        }

        String[] tokens = cleaned.split(",");

        if (tokens.length < 2) {
            throw new IllegalArgumentException(
                    "La instancia TSP debe contener al menos dos ciudades"
            );
        }

        int[] cityIds = new int[tokens.length];

        Set<Integer> seenIds = new HashSet<>();

        for (int i = 0; i < tokens.length; i++) {

            String token = tokens[i].trim();

            if (token.isEmpty()) {
                throw new IllegalArgumentException(
                        "Se encontró un ID vacío en la posición " + i
                );
            }

            final int cityId;

            try {
                cityId = Integer.parseInt(token);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "ID de ciudad inválido: " + token,
                        e
                );
            }

            if (cityId <= 0) {
                throw new IllegalArgumentException(
                        "El ID de ciudad debe ser positivo: " + cityId
                );
            }

            if (!seenIds.add(cityId)) {
                throw new IllegalArgumentException(
                        "La ciudad está repetida en la instancia: " + cityId
                );
            }

            cityIds[i] = cityId;
        }

        return cityIds;
    }
}