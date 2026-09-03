package mx.unam.heuristicas.tsp;

import org.junit.jupiter.api.Test;

import mx.unam.heuristicas.exception.AppException;

import java.net.URISyntaxException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class TspFileReaderTest {

        @Test
        void readShouldReturnCityIdsInOriginalOrder()
                        throws URISyntaxException {

                Path path = getResourcePath("valid.tsp");

                int[] result = TspFileReader.read(path);

                assertArrayEquals(
                                new int[] { 108, 7, 934, 25 },
                                result);
        }

        @Test
        void readShouldAllowTrailingComma()
                        throws URISyntaxException {

                Path path = getResourcePath("trailing-comma.tsp");

                int[] result = TspFileReader.read(path);

                assertArrayEquals(
                                new int[] { 1, 2, 3, 4 },
                                result);
        }

        @Test
        void readShouldIgnoreSpaces()
                        throws URISyntaxException {

                Path path = getResourcePath("spaces.tsp");

                int[] result = TspFileReader.read(path);

                assertArrayEquals(
                                new int[] { 1, 2, 3, 4 },
                                result);
        }

        @Test
        void readShouldRejectDuplicateCities()
                        throws URISyntaxException {

                Path path = getResourcePath("duplicates.tsp");

                assertThrows(
                                AppException.class,
                                () -> TspFileReader.read(path));
        }

        @Test
        void readShouldRejectInvalidId()
                        throws URISyntaxException {

                Path path = getResourcePath("invalid-id.tsp");

                assertThrows(
                                AppException.class,
                                () -> TspFileReader.read(path));
        }

        @Test
        void readShouldRejectEmptyFile()
                        throws URISyntaxException {

                Path path = getResourcePath("empty.tsp");

                assertThrows(
                                AppException.class,
                                () -> TspFileReader.read(path));
        }

        @Test
        void readShouldRejectNonExistingFile() {

                Path path = Path.of(
                                "archivo-que-no-existe.tsp");

                assertThrows(
                                AppException.class,
                                () -> TspFileReader.read(path));
        }

        private Path getResourcePath(String fileName)
                        throws URISyntaxException {

                return Path.of(
                                getClass()
                                                .getResource("/" + fileName)
                                                .toURI());
        }
}