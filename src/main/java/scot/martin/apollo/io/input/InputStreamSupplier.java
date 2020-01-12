package scot.martin.apollo.io.input;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class InputStreamSupplier implements Supplier<Optional<InputStream>> {

    private static final Logger LOGGER = Logger.getLogger("InputStreamSupplier");
    private final String url;

    public InputStreamSupplier(String url) {
        this.url = url;
    }

    @Override
    public Optional<InputStream> get() {
        Optional<InputStream> result = null;

        try {
            LOGGER.info("Creating input stream from " + url);

            InputStream urlInputStream = new URL(url).openStream();
            InputStream bufferedInputStream = new BufferedInputStream(urlInputStream);
            result = Optional.of(bufferedInputStream);

            LOGGER.info("Input stream created");
        } catch (IOException e) {
            LOGGER.severe("Failed to create input stream: " + e.getMessage());
        }

        return result;
    }
}
