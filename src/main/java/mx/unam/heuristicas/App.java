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

                if (args.length != 2) {
                        throw new AppException(
                                        """
                                                        Uso:
                                                                java -jar programa.jar n archivo.tsp
                                                                java -jar programa.jar e archivo.tsp
                                                        """);
                }

                String mode = args[0].trim().toLowerCase();

                Path tspPath = Path.of(args[1]);

                switch (mode) {
                        case "-n" ->
                                NormalRunner.runNormal(tspPath);

                        case "-e" ->
                                ExperimentRunner.runExperiment(tspPath);

                        default ->
                                throw new AppException(
                                                "Modo desconocido: " + mode);
                }
        }
}