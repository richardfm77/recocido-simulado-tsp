package mx.unam.heuristicas.util;

import java.util.Collections;

public class HelperBD {
    public static String createPlaceholders(int amount) {
        return String.join(
                ",",
                Collections.nCopies(
                        amount,
                        "?"));
    }
}
