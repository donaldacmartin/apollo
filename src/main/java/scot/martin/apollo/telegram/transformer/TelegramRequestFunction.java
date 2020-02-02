package scot.martin.apollo.telegram.transformer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

public class TelegramRequestFunction implements Function<String, Optional<String>> {

    private static final Logger LOGGER = Logger.getLogger("TelegramRequestFunction");
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private static final String BASE_URL = "https://api.telegram.org/";
    private static final String API_KEY = "API_KEY_HERE";

    @Override
    public Optional<String> apply(String qry) {
        Optional<String> body = Optional.empty();

        try {
            StringBuilder uri = new StringBuilder()
                    .append(BASE_URL)
                    .append(API_KEY)
                    .append("/")
                    .append(qry);

            HttpRequest request = HttpRequest
                    .newBuilder()
                    .GET()
                    .uri(URI.create(uri.toString()))
                    .timeout(TIMEOUT)
                    .build();

            HttpResponse<String> response = HttpClient
                    .newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            body = Optional.of(response.body());
        } catch (IOException | InterruptedException ie) {
            LOGGER.severe("Error calling Telegram: " + ie.getMessage());
        }

        return body;
    }
}

