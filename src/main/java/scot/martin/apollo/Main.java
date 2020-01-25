package scot.martin.apollo;

import scot.martin.apollo.function.transformer.ArgsShowFunction;
import scot.martin.apollo.function.transformer.TimeTilBroadcastFunction;
import scot.martin.apollo.model.Show;
import scot.martin.apollo.predicate.UpcomingShowPredicate;
import scot.martin.apollo.worker.Downloader;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger("Main");

    private static final int SCHEDULER_SLEEP = 5 * 60 * 1000;
    private static final int POOL_SIZE = 10;

    public static void main(String[] args) {
        if (args.length < 5) {
            System.out.println("Usage: Name Time Timezone Duration URL");
            System.exit(-1);
        }

        Function<List<String>, Show> argsShowFunction = new ArgsShowFunction();
        Predicate<Show> upcomingShowPredicate = new UpcomingShowPredicate(SCHEDULER_SLEEP);
        Function<Show, Long> timeTilBroadcastFunction = new TimeTilBroadcastFunction();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(POOL_SIZE);

        try {
            Show show = argsShowFunction.apply(List.of(args));

            if (show == null) {
                executorService.shutdown();
                System.out.println("Couldn't parse show");
                System.exit(-1);
            }

            while (true) {
                LOGGER.info("Checking if show can be scheduled");

                if (upcomingShowPredicate.test(show)) {
                    LOGGER.info("Show " + show.getName() + " can be scheduled");

                    Callable<Optional<String>> downloader = new Downloader(show);
                    long delayMillis = timeTilBroadcastFunction.apply(show);

                    executorService.schedule(downloader, delayMillis, TimeUnit.MILLISECONDS);
                    LOGGER.info("Show " + show.getName() + " scheduled");
                }

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
