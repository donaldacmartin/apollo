package scot.martin.apollo.io.output;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class FileNameSupplier implements Supplier<Optional<String>> {

    private static final Logger LOGGER = Logger.getLogger("FileNameSupplier");
    private static final String DEFAULT_PREFIX = "Downloader";
    private static final String DEFAULT_SUFFIX = ".mp3";

    @Override
    public Optional<String> get() {
        Optional<String> result = Optional.empty();

        try {
            LOGGER.info("Creating temp file");

            File file = File.createTempFile(DEFAULT_PREFIX, DEFAULT_SUFFIX);
            result = Optional.of(file.getAbsolutePath());

            LOGGER.info("Temp file created");
        } catch (IOException e) {
            LOGGER.severe("Failed to create temp file: " + e.getMessage());
        }

        return result;
    }
}
