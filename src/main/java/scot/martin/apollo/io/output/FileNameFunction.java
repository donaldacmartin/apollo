package scot.martin.apollo.io.output;

import scot.martin.apollo.model.Show;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

public class FileNameFunction implements Function<Show, Optional<Path>> {

    private static final Logger LOGGER = Logger.getLogger("FileNameFunction");
    private static final String DEFAULT_EXTENSION = ".mp3";

    @Override
    public Optional<Path> apply(Show show) {
        Optional<Path> result = Optional.empty();

        if (show != null && show.getName() != null) {
            try {
                LOGGER.fine("Creating temp file");

                String prefix = show.getName();
                File file = File.createTempFile(prefix, DEFAULT_EXTENSION);
                result = Optional.of(file.toPath());

                LOGGER.fine("Temp file created");
            } catch (IOException e) {
                LOGGER.severe("Failed to create temp file: " + e.getMessage());
            }
        } else {
            LOGGER.warning("Cannot create temp file from null");
        }

        return result;
    }
}
