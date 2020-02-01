package scot.martin.apollo.io.input;

import scot.martin.apollo.model.Show;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

public class InputStreamFunction implements Function<Show, Optional<InputStream>> {

    private static final Logger LOGGER = Logger.getLogger("InputStreamFunction");

    @Override
    public Optional<InputStream> apply(Show show) {
        Optional<InputStream> result = Optional.empty();

        if (show != null) {
            try {
                String url = show.getUrl();
                LOGGER.fine("Creating input stream from " + url);

                InputStream urlInputStream = new URL(url).openStream();
                InputStream bufferedInputStream = new BufferedInputStream(urlInputStream);
                result = Optional.of(bufferedInputStream);

                LOGGER.fine("Input stream created");
            } catch (IOException e) {
                LOGGER.severe("Failed to create input stream: " + e.getMessage());
            }
        } else {
            LOGGER.warning("Cannot create input stream from null");
        }

        return result;
    }
}
