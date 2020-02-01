package scot.martin.apollo.io.move;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class WebDirectoryFunction implements Function<Path, Optional<Path>> {

    private static final Logger LOGGER = Logger.getLogger("WebDirectoryFunction");
    private static final Path WEB_DIR = Paths.get("/var/www/html");

    private final Predicate<Path> isWritableDir;

    public WebDirectoryFunction() {
        this.isWritableDir = p -> p.toFile().isDirectory() && p.toFile().canWrite();
    }

    @Override
    public Optional<Path> apply(Path source) {
        Optional<Path> newLocation = Optional.empty();

        if (source != null) {
            try {
                if (isWritableDir.test(WEB_DIR)) {
                    if (source.toFile().isFile() && source.toFile().canRead()) {
                        Path fileName = source.getFileName();
                        Path fileInDestination = WEB_DIR.resolve(fileName);

                        Files.move(source, fileInDestination);
                        newLocation = Optional.of(fileInDestination.toAbsolutePath());

                        LOGGER.info("File moved from " + source + " to " + fileInDestination);
                    } else {
                        LOGGER.warning("Source is not a readable file");
                    }
                } else {
                    LOGGER.warning("Destination is not a writeable directory");
                }
            } catch (IOException io) {
                LOGGER.severe("Error moving file: " + io.getMessage());
            }
        } else {
            LOGGER.warning("Cannot move null source");
        }

        return newLocation;
    }
}
