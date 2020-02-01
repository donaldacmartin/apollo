package scot.martin.apollo.thread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * Thread to clean up old files and prevent the web directory from becoming
 * saturated.
 *
 * @author Donald AC Martin
 * @since 1.0.0
 */
public final class JanitorThread implements Runnable {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger("JanitorThread");

    /**
     * Directory in which our files are stored.
     */
    private static final Path WEB_DIR = Paths.get("/var/www/html");

    /**
     * The maximum age of a file before deletion in milliseconds.
     */
    private static final int MAX_AGE = 24 * 60 * 60 * 1000;

    /**
     * The time to wait between checks in milliseconds.
     */
    private static final int SLEEP_TIME = 60 * 60 * 1000;

    /**
     * A predicate to determine if a file is old enough to be deleted.
     */
    private final Predicate<File> isTooOld;

    /**
     * No args constructor that defines the old age predicate.
     */
    public JanitorThread() {
        this.isTooOld = f ->
                f.lastModified() < System.currentTimeMillis() - MAX_AGE;
    }

    @Override
    public void run() {
        while (true) {
            try {
                LOGGER.info("Looking for files to delete");

                Files.list(WEB_DIR)
                        .map(Path::toFile)
                        .filter(File::isFile)
                        .filter(File::canWrite)
                        .filter(isTooOld)
                        .forEach(File::delete);

                LOGGER.info("Finished search");
                Thread.sleep(SLEEP_TIME);
            } catch (IOException e) {
                LOGGER.warning("Error deleting files: " + e.getMessage());
            } catch (InterruptedException e) {
                LOGGER.warning("Thread interrupted: " + e.getMessage());
            }
        }
    }
}
