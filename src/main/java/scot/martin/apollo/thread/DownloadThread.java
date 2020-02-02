package scot.martin.apollo.thread;

import scot.martin.apollo.io.download.DownloadFunction;
import scot.martin.apollo.model.BroadcastDownload;
import scot.martin.apollo.model.Show;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Wrapper thread for the download function. Takes a show, downloads it, moves
 * it to the web directory, then returns that path if successful. The show
 * will be downloaded immediately on call.
 *
 * @author Donald AC Martin
 * @since 1.0.0
 */
public final class DownloadThread implements Callable<Optional<Path>> {

    /**
     * Logger to record operations.
     */
    private static final Logger LOGGER = Logger.getLogger("DownloadThread");

    private static final String SERVER = "SERVER_URL";

    /**
     * A function that takes a show and returns file location.
     */
    private final Function<Show, Optional<Path>> downloadFunction;

    /**
     * The show to be downloaded.
     */
    private final Show show;

    private final BlockingQueue<BroadcastDownload> downloads;

    /**
     * Construct the thread with a show to download.
     *
     * @param showToDownload Show to be download
     */
    public DownloadThread(final Show showToDownload,
                          final BlockingQueue<BroadcastDownload> downloads) {
        this.downloadFunction = new DownloadFunction();
        this.show = showToDownload;
        this.downloads = downloads;
    }

    @Override
    public Optional<Path> call() {
        LOGGER.info("Starting download thread for " + show.getName());
        Optional<Path> fileLocation = downloadFunction.apply(show);
        LOGGER.info("Download ended for " + show.getName());

        if (fileLocation.isPresent()) {
            String fileName = fileLocation.get().getFileName().toString();
            String url = SERVER + fileName;
            String name = show.getName();

            downloads.add(new BroadcastDownload(name, url));
        }

        return fileLocation;
    }
}
