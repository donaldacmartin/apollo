package scot.martin.apollo.worker.maintenance;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class JanitorThread implements Runnable {

    private static final Logger LOGGER = Logger.getLogger("JanitorThread");

    private static final Path DEFAULT_PATH = Paths.get("/var/www/html");
    private static final int DEFAULT_MAX_AGE = 24;
    private static final int DEFAULT_MINS = 60;

    private final Path path;
    private final Predicate<File> isTooOld;
    private final int millisBetweenRuns;

    public static JanitorThread create() {
        return new JanitorThread(DEFAULT_PATH, DEFAULT_MAX_AGE, DEFAULT_MINS);
    }

    private JanitorThread(Path path, int maxAgeHours, int minsBetweenRuns) {
        int maxAgeMillis = maxAgeHours * 60 * 60 * 1000;

        this.path = path;
        this.isTooOld = f -> f.lastModified() < System.currentTimeMillis() - maxAgeMillis;
        this.millisBetweenRuns = minsBetweenRuns * 60 * 1000;
    }

    @Override
    public void run() {
        while (true) {
            try {
                LOGGER.info("Looking for files to delete");

                Files.list(path)
                        .map(Path::toFile)
                        .filter(File::isFile)
                        .filter(File::canWrite)
                        .filter(isTooOld)
                        .forEach(File::delete);

                LOGGER.info("Finished search");
                Thread.sleep(millisBetweenRuns);
            } catch (IOException e) {
                LOGGER.warning("Error while deleting file(s): " + e.getMessage());
            } catch (InterruptedException e) {
                LOGGER.warning("Thread interrupted: " + e.getMessage());
            }
        }
    }
}
