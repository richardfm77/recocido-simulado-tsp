package mx.unam.heuristicas.report;

import mx.unam.heuristicas.exception.AppException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public final class AcceptedEvaluationsReportWriter
        implements AutoCloseable {

    private final BufferedWriter writer;

    public AcceptedEvaluationsReportWriter(Path reportDirectory) {

        Objects.requireNonNull(
                reportDirectory,
                "El directorio de reportes no puede ser null"
        );

        try {

            Files.createDirectories(reportDirectory);

            Path reportPath =
                    reportDirectory.resolve(
                            "evaluaciones-aceptadas.txt"
                    );

            writer = Files.newBufferedWriter(
                    reportPath,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

        } catch (IOException e) {

            throw new AppException(
                    "No se pudo crear el reporte de evaluaciones aceptadas",
                    e
            );
        }
    }

    public void write(double evaluation) {

        try {

            writer.write(
                    "E:" + evaluation
            );

            writer.newLine();

        } catch (IOException e) {

            throw new AppException(
                    "No se pudo escribir una evaluación aceptada",
                    e
            );
        }
    }

    @Override
    public void close() {

        try {

            writer.close();

        } catch (IOException e) {

            throw new AppException(
                    "No se pudo cerrar el reporte de evaluaciones aceptadas",
                    e
            );
        }
    }
}