package scot.martin.apollo.io.input;

import scot.martin.apollo.model.Show;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ShowSupplier implements Supplier<Collection<Show>> {

    private static final Logger LOGGER = Logger.getLogger("ShowSupplier");
    private static final String SEPARATOR = ",";

    private final Function<List<String>, Show> argsShowFunction;
    private final Function<String, List<String>> csvRowSplitFunction;
    private final Path path;

    public ShowSupplier(Path path) {
        this.argsShowFunction = new ArgsShowFunction();
        this.csvRowSplitFunction = r -> List.of(r.split(SEPARATOR));
        this.path = path;
    }

    @Override
    public Collection<Show> get() {
        Collection<Show> shows = new HashSet<>();
        File file = path.toFile();

        if (file.exists() && file.canRead()) {
            try {
                shows = Files
                        .lines(path)
                        .skip(1)
                        .map(csvRowSplitFunction)
                        .map(argsShowFunction)
                        .collect(Collectors.toSet());

                LOGGER.info("Read " + shows.size() + " shows");
            } catch (IOException io) {
                LOGGER.severe("Failed to read shows: " + io.getMessage());
            }
        } else {
            LOGGER.severe("File doesn't exist or is unreadable");
        }

        return shows;
    }
}
