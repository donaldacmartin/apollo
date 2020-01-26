package scot.martin.apollo.worker;

import scot.martin.apollo.function.timing.TimeFunction;
import scot.martin.apollo.io.FileMover;
import scot.martin.apollo.io.input.InputStreamSupplier;
import scot.martin.apollo.io.output.FileNameSupplier;
import scot.martin.apollo.io.output.OutputStreamFunction;
import scot.martin.apollo.model.Show;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class Downloader implements Callable<Optional<String>> {

    private static final Logger LOGGER = Logger.getLogger("Downloader");
    private static final long MAX_BYTES = 209715200;
    private static final Path DESTINATION = Paths.get("/var/www/html");

    private final Supplier<Optional<InputStream>> inputStreamSupplier;
    private final Supplier<Optional<String>> fileNameSupplier;
    private final Function<String, Optional<OutputStream>> outputStreamFunction;
    private final Function<Long, LocalDateTime> timeFunction;
    private final Consumer<Path> fileMover;

    private final long durationMillis;
    private final long maxBytes;

    public Downloader(Show show) {
        this.inputStreamSupplier = new InputStreamSupplier(show.getUrl());
        this.fileNameSupplier = new FileNameSupplier(show.getName());
        this.outputStreamFunction = new OutputStreamFunction();
        this.timeFunction = new TimeFunction();
        this.fileMover = new FileMover(DESTINATION);

        this.durationMillis = show.getMinutes() * 60 * 1000;
        this.maxBytes = MAX_BYTES;
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
                    LOGGER.info("Streamed to " + optionalFileName.get());
                    fileMover.accept(Paths.get(optionalFileName.get()));
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
        long endTime = System.currentTimeMillis() + durationMillis;
        LOGGER.info("Starting streaming until " + timeFunction.apply(endTime));
        long bytesRead = 0;

        while (endTime > System.currentTimeMillis() && bytesRead < maxBytes) {
            outputStream.write(inputStream.read());
            bytesRead++;
        }

        LOGGER.info("Streaming ended");
    }

    private void close(InputStream inputStream, OutputStream outputStream) {
        try {
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            LOGGER.fine("Streams closed");
        } catch (IOException e) {
            LOGGER.severe("Error closing streams: " + e.getMessage());
        }
    }
}
