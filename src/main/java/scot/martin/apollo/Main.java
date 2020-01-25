package scot.martin.apollo;

import scot.martin.apollo.function.transformer.TimeTilBroadcastFunction;
import scot.martin.apollo.io.input.ShowSupplier;
import scot.martin.apollo.model.Show;
import scot.martin.apollo.predicate.UpcomingShowPredicate;
import scot.martin.apollo.worker.Downloader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger("Main");

    private static final int SCHEDULER_SLEEP = 10 * 60 * 1000;
    private static final int POOL_SIZE = 10;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: PATH_TO_CSV");
            System.exit(-1);
        }

        Path csvPath = Paths.get(args[0]);
        Supplier<Collection<Show>> showSupplier = new ShowSupplier(csvPath);

        Predicate<Show> upcomingShowPredicate = new UpcomingShowPredicate(SCHEDULER_SLEEP);
        Function<Show, Long> timeTilBroadcastFunction = new TimeTilBroadcastFunction();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(POOL_SIZE);

        try {
            while (true) {
                LOGGER.info("Checking if shows can be scheduled");

                showSupplier.get().parallelStream().filter(upcomingShowPredicate).forEach(s -> {
                    LOGGER.info("Show " + s.getName() + " can be scheduled");

                    Callable<Optional<String>> downloader = new Downloader(s);
                    long delayMillis = timeTilBroadcastFunction.apply(s);

                    executorService.schedule(downloader, delayMillis, TimeUnit.MILLISECONDS);
                    LOGGER.info("Show " + s.getName() + " scheduled");
                });

                LOGGER.info("Scheduler sleeping...");
                Thread.sleep(SCHEDULER_SLEEP);
            }
        } catch (Exception e) {
            LOGGER.severe("Error while streaming: " + e.getMessage());
            executorService.shutdown();
            System.exit(-1);
        }
    }
}
