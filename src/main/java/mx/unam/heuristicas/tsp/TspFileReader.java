package mx.unam.heuristicas.tsp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import mx.unam.heuristicas.exception.AppException;

public final class TspFileReader {

    private TspFileReader() {
    }

    public static int[] read(Path path) throws IllegalArgumentException, AppException{

        if (path == null) {
            throw new IllegalArgumentException(
                    "La ruta del archivo no puede ser null"
            );
        }

        String content;

        try {
            content = Files.readString(path);
        } catch (IOException e) {
            throw new AppException(
                    "No se pudo leer el archivo TSP: " + path,
                    e
            );
        }

        return parse(content);
    }

    private static int[] parse(String content) {

        if (content == null || content.isBlank()) {
            throw new AppException(
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
            throw new AppException(
                    "La instancia TSP debe contener al menos dos ciudades"
            );
        }

        int[] cityIds = new int[tokens.length];

        Set<Integer> seenIds = new HashSet<>();

        for (int i = 0; i < tokens.length; i++) {

            String token = tokens[i].trim();

            if (token.isEmpty()) {
                throw new AppException(
                        "Se encontró un ID vacío en la posición " + i
                );
            }

            final int cityId;

            try {
                cityId = Integer.parseInt(token);
            } catch (NumberFormatException e) {
                throw new AppException(
                        "ID de ciudad inválido: " + token,
                        e
                );
            }

            if (cityId <= 0) {
                throw new AppException(
                        "El ID de ciudad debe ser positivo: " + cityId
                );
            }

            if (!seenIds.add(cityId)) {
                throw new AppException(
                        "La ciudad está repetida en la instancia: " + cityId
                );
            }

            cityIds[i] = cityId;
        }

        return cityIds;
    }
}