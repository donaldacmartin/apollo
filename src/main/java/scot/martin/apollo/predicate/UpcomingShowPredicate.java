package scot.martin.apollo.predicate;

import scot.martin.apollo.model.Show;
import scot.martin.apollo.function.transformer.TimeTilBroadcastFunction;

import java.util.function.Function;
import java.util.function.Predicate;

public class UpcomingShowPredicate implements Predicate<Show> {

    private final Function<Show, Long> timeTilBroadcastFunction;
    private final int threshold;

    public UpcomingShowPredicate(int thresholdMillis) {
        this.timeTilBroadcastFunction = new TimeTilBroadcastFunction();
        this.threshold = thresholdMillis;
    }

    @Override
    public boolean test(Show show) {
        long millisUntilBroadcast = timeTilBroadcastFunction.apply(show);
        return millisUntilBroadcast >= 0 && millisUntilBroadcast < threshold;
    }

}
