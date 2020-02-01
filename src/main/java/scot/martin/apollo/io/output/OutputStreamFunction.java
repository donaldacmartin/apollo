package scot.martin.apollo.io.output;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

public class OutputStreamFunction implements Function<Optional<Path>, Optional<OutputStream>> {

    private static final Logger LOGGER = Logger.getLogger("OutputStreamFunction");

    @Override
    public Optional<OutputStream> apply(Optional<Path> path) {
        Optional<OutputStream> result = Optional.empty();

        if (path != null || path.isEmpty()) {
            try {
                LOGGER.fine("Creating output stream");

                FileOutputStream outputStream = new FileOutputStream(path.get().toFile());
                result = Optional.of(outputStream);

                LOGGER.fine("Output stream created");
            } catch (IOException e) {
                LOGGER.severe("Failed to create output stream: " + e.getMessage());
            }
        } else {
            LOGGER.warning("Cannot create output stream without path");
        }

        return result;
    }
}
