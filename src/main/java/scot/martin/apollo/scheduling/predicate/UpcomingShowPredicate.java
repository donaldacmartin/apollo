package scot.martin.apollo.scheduling.predicate;

import scot.martin.apollo.model.Show;
import scot.martin.apollo.scheduling.function.TimeUntilBroadcastFunction;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A predicate to determine if a given show is worth scheduling.
 *
 * @author Donald AC Martin
 * @since 1.0.0
 */
public final class UpcomingShowPredicate implements Predicate<Show> {

    /**
     * Function to get milliseconds until show's start time.
     */
    private final Function<Show, Long> timeUntilBroadcastFunction;

    /**
     * Number of milliseconds in advance to consider show worth scheduling.
     */
    private final int threshold;

    /**
     * Constructor to set scope of scheduleability.
     *
     * @param thresholdMillis Number of milliseconds to consider in scope
     */
    public UpcomingShowPredicate(final int thresholdMillis) {
        this.timeUntilBroadcastFunction = new TimeUntilBroadcastFunction();
        this.threshold = thresholdMillis;
    }

    @Override
    public boolean test(final Show show) {
        long millisUntilBroadcast = timeUntilBroadcastFunction.apply(show);
        return millisUntilBroadcast >= 0 && millisUntilBroadcast < threshold;
    }

}
