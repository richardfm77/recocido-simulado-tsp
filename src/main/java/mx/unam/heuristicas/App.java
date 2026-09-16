package mx.unam.heuristicas;

import mx.unam.heuristicas.exception.AppException;

import java.nio.file.Path;

public final class App {

        private App() {
        }

        public static void main(String[] args) {

                try {

                        run(args);

                } catch (AppException e) {

                        System.err.println(
                                        "Error: " + e.getMessage());

                        throw e;

                } catch (IllegalArgumentException e) {

                        System.err.println(
                                        "Argumento inválido: "
                                                        + e.getMessage());

                        throw e;

                } catch (Exception e) {

                        System.err.println(
                                        "Ocurrió un error inesperado");

                        e.printStackTrace();

                        throw e;
                }
        }

        private static void run(String[] args) {

                if ((args.length == 1) && args[0].equalsIgnoreCase("-h")) {
                        System.out.println(getUsage());
                        return;
                } else if (args.length != 4) {
                        throw new AppException(
                                        "Número de argumentos inválido. "
                                                        + "Se esperaban 4 argumentos. \n"
                                                        + getUsage());
                }

                String mode = args[0].trim().toLowerCase();

                Path tspPath = Path.of(args[1]);

                Path propertiesPath = Path.of(args[2]);

                Path reportDirectory = Path.of(args[3]);

                switch (mode) {
                        case "-n" ->
                                NormalRunner.runNormal(tspPath, propertiesPath, reportDirectory);

                        case "-e" ->
                                ExperimentRunner.runExperiment(tspPath, propertiesPath, reportDirectory);

                        default ->
                                throw new AppException(
                                                "Modo desconocido: " + mode);
                }
        }

        private static String getUsage() {
                return """
                                Uso:
                                        java -jar programa.jar -n archivo.tsp parametros.properties ruta-reportes/
                                        java -jar programa.jar -e archivo.tsp parametros.properties ruta-reportes/
                                """;
        }
}