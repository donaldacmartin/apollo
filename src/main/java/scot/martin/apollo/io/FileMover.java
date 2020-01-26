package scot.martin.apollo.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class FileMover implements Consumer<Path> {

    private static final Logger LOGGER = Logger.getLogger("FileMover");

    private final Path destination;
    private final Predicate<Path> isWritableDir;

    public FileMover(Path destination) {
        this.destination = destination;
        this.isWritableDir = p -> p.toFile().isDirectory() && p.toFile().canWrite();

    }

    @Override
    public void accept(Path source) {
        try {
            if (isWritableDir.test(destination)) {
                if (source.toFile().isFile() && source.toFile().canRead()) {
                    Path fileName = source.getFileName();
                    Path fileInDestination = destination.resolve(fileName);

                    Files.move(source, fileInDestination);
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
    }
}
