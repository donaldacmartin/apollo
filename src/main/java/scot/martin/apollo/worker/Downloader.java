package scot.martin.apollo.worker;

import scot.martin.apollo.io.input.InputStreamSupplier;
import scot.martin.apollo.io.output.FileNameSupplier;
import scot.martin.apollo.io.output.OutputStreamFunction;
import scot.martin.apollo.timing.TimeFunction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class Downloader implements Callable<Optional<String>> {

    private static final Logger LOGGER = Logger.getLogger("Downloader");

    private final Supplier<Optional<InputStream>> inputStreamSupplier;
    private final Supplier<Optional<String>> fileNameSupplier;
    private final Function<String, Optional<OutputStream>> outputStreamFunction;
    private final Function<Long, LocalDateTime> timeFunction;

    private final long endTime;

    public Downloader(String url, long duration) {
        this.inputStreamSupplier = new InputStreamSupplier(url);
        this.fileNameSupplier = new FileNameSupplier();
        this.outputStreamFunction = new OutputStreamFunction();
        this.timeFunction = new TimeFunction();
        this.endTime = System.currentTimeMillis() + duration;
    }

    @Override
    public Optional<String> call() {
        Optional<InputStream> optionalInputStream = inputStreamSupplier.get();
        Optional<String> optionalFileName = fileNameSupplier.get();

        if (optionalInputStream.isPresent() && optionalFileName.isPresent()) {
            Optional<OutputStream> optionalOutputStream = outputStreamFunction.apply(optionalFileName.get());

            if (optionalOutputStream.isPresent()) {
                InputStream inputStream = optionalInputStream.get();
                OutputStream outputStream = optionalOutputStream.get();

                try {
                    stream(inputStream, outputStream);
                } catch (IOException e) {
                    LOGGER.severe("Error while streaming: " + e.getMessage());
                } finally {
                    close(inputStream, outputStream);
                }
            } else {
                LOGGER.severe("Missing output stream");
            }
        } else {
            LOGGER.severe("Missing input stream or output filename");
        }

        return optionalFileName;
    }

    private void stream(InputStream inputStream, OutputStream outputStream) throws IOException {
        LOGGER.info("Starting streaming until " + timeFunction.apply(endTime));

        while (endTime > System.currentTimeMillis()) {
            outputStream.write(inputStream.read());
        }

        LOGGER.info("Streaming ended");
    }

    private void close(InputStream inputStream, OutputStream outputStream) {
        try {
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            LOGGER.info("Streams closed");
        } catch (IOException e) {
            LOGGER.severe("Error closing streams: " + e.getMessage());
        }
    }
}
