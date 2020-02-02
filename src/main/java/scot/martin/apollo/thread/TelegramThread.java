package scot.martin.apollo.thread;

import scot.martin.apollo.model.BroadcastDownload;
import scot.martin.apollo.telegram.io.TelegramDao;
import scot.martin.apollo.telegram.transformer.DownloadMsgFunction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TelegramThread implements Runnable {

    private static final Logger LOGGER = Logger.getLogger("TelegramThread");
    private static final int THREAD_SLEEP = 60 * 1000;
    private static final String SUBSCRIBE_MSG = "Vous êtes maintenant abonné(e)";
    private static final String SUBSCRIBED_MSG = "Vous êtes déjà abonné(e)";

    private final Supplier<Set<Long>> subscriberSupplier;
    private final BiConsumer<Set<Long>, String> messageSender;
    private final Set<Long> subscribers;
    private final BlockingQueue<BroadcastDownload> downloadQueue;
    private final Function<BroadcastDownload, String> downloadMsgFunction;

    public TelegramThread(final BlockingQueue<BroadcastDownload> downloadQueue) {
        TelegramDao dao = new TelegramDao();

        this.subscriberSupplier = dao;
        this.messageSender = dao;
        this.subscribers = new HashSet<>();
        this.downloadQueue = downloadQueue;
        this.downloadMsgFunction = new DownloadMsgFunction();
    }

    @Override
    public void run() {
        while (true) {
            try {
                LOGGER.info("Running...");
                updateSubscribers();

                List<BroadcastDownload> downloads = new ArrayList<>();
                downloadQueue.drainTo(downloads);

                LOGGER.info("Currently have " + downloads.size() + " subscribers");

                downloads
                        .stream()
                        .map(downloadMsgFunction)
                        .forEach(m -> messageSender.accept(subscribers, m));

                Thread.sleep(THREAD_SLEEP);
                LOGGER.info("Sleeping...");
            } catch (Exception e) {
                LOGGER.severe("Error in thread: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void updateSubscribers() {
        LOGGER.info("Checking for new subscribers");
        Set<Long> updateSubscribers = subscriberSupplier.get();

        Set<Long> newSubscribers = updateSubscribers
                .parallelStream()
                .filter(s -> !subscribers.contains(s))
                .collect(Collectors.toSet());

        Set<Long> existingSubscribers = updateSubscribers
                .parallelStream()
                .filter(subscribers::contains)
                .collect(Collectors.toSet());

        subscribers.addAll(newSubscribers);
        messageSender.accept(newSubscribers, SUBSCRIBE_MSG);
        messageSender.accept(existingSubscribers, SUBSCRIBED_MSG);
        LOGGER.info("Finished check for subscribers");
    }
}
