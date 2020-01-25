package scot.martin.apollo.io.output;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class FileNameSupplier implements Supplier<Optional<String>> {

    private static final Logger LOGGER = Logger.getLogger("FileNameSupplier");
    private static final String DEFAULT_SUFFIX = ".mp3";

    private final String prefix;

    public FileNameSupplier(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Optional<String> get() {
        Optional<String> result = Optional.empty();

        try {
            LOGGER.fine("Creating temp file");

            File file = File.createTempFile(prefix, DEFAULT_SUFFIX);
            result = Optional.of(file.getAbsolutePath());

            LOGGER.fine("Temp file created");
        } catch (IOException e) {
            LOGGER.severe("Failed to create temp file: " + e.getMessage());
        }

        return result;
    }
}
