package scot.martin.apollo.io.download;

import scot.martin.apollo.io.input.InputStreamFunction;
import scot.martin.apollo.io.move.WebDirectoryFunction;
import scot.martin.apollo.io.output.FileNameFunction;
import scot.martin.apollo.io.output.OutputStreamFunction;
import scot.martin.apollo.model.Show;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

public final class DownloadFunction implements Function<Show, Optional<Path>> {

    private static final Logger LOGGER = Logger.getLogger("DownloadFunction");
    private static final int MAX_BYTES = 200 * 1024 * 1024;

    private final Function<Show, Optional<InputStream>> inputStreamFunction;
    private final Function<Show, Optional<Path>> fileNameFunction;
    private final Function<Optional<Path>, Optional<OutputStream>> outputStreamFunction;
    private final Function<Path, Optional<Path>> webDirectoryFunction;

    public DownloadFunction() {
        inputStreamFunction = new InputStreamFunction();
        fileNameFunction = new FileNameFunction();
        outputStreamFunction = new OutputStreamFunction();
        webDirectoryFunction = new WebDirectoryFunction();
    }

    @Override
    public Optional<Path> apply(Show show) {
        Optional<Path> outputPath = Optional.empty();

        if (show != null) {
            Optional<InputStream> inputStream = inputStreamFunction.apply(show);
            Optional<Path> file = fileNameFunction.apply(show);
            Optional<OutputStream> outputStream = outputStreamFunction.apply(file);

            if (inputStream.isPresent() && outputStream.isPresent()) {
                try (InputStream input = inputStream.get();
                     OutputStream output = outputStream.get()) {
                    stream(show, input, output);
                    outputPath = webDirectoryFunction.apply(file.get());
                } catch (IOException io) {
                    LOGGER.severe("Error downloading: " + io.getMessage());
                }
            } else {
                LOGGER.warning("Couldn't get one of the streams");
            }
        } else {
            LOGGER.warning("Cannot download null");
        }

        return outputPath;
    }

    private void stream(Show show, InputStream is, OutputStream os)
            throws IOException {
        long endTime = System.currentTimeMillis() + show.getMillis();
        LOGGER.info("Streaming for " + show.getMinutes() + " mins");

        long bytesRead = 0;

        while (endTime > System.currentTimeMillis() && bytesRead < MAX_BYTES) {
            os.write(is.read());
            bytesRead++;
        }
    }
}
