package scot.martin.apollo.io.output;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

public class OutputStreamFunction implements Function<String, Optional<OutputStream>> {

    private static final Logger LOGGER = Logger.getLogger("OutputStreamFunction");

    @Override
    public Optional<OutputStream> apply(String fileName) {
        Optional<OutputStream> result = Optional.empty();

        try {
            LOGGER.fine("Creating output stream");

            Path path = Paths.get(fileName);
            FileOutputStream outputStream = new FileOutputStream(path.toFile());
            result = Optional.of(outputStream);

            LOGGER.fine("Output stream created");
        } catch (IOException e) {
            LOGGER.severe("Failed to create output stream: " + e.getMessage());
        }

        return result;
    }
}
