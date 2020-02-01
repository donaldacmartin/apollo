package scot.martin.apollo.scheduling.function;

import scot.martin.apollo.model.Show;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

/**
 * A function to return the number of milliseconds until the show's start time.
 *
 * @author Donald AC Martin
 * @since 1.0.0
 */
public final class TimeUntilBroadcastFunction implements Function<Show, Long> {

    /**
     * A function to get a LocalDateTime from a given time zone.
     */
    private final Function<ZoneId, LocalDateTime> currentTimeFunction;

    /**
     * Empty constructor that initialises the final function.
     */
    public TimeUntilBroadcastFunction() {
        this.currentTimeFunction = t -> ZonedDateTime.now(t).toLocalDateTime();
    }

    @Override
    public Long apply(final Show show) {
        LocalTime broadcastTime = show.getTime();
        ZoneId timeZone = show.getTimeZone();
        LocalDateTime currentDateTime = currentTimeFunction.apply(timeZone);

        DayOfWeek dayOfWeek = currentDateTime.getDayOfWeek();
        LocalTime currentTime = currentDateTime.toLocalTime();

        if (show.getDaysOfWeek().contains(dayOfWeek)) {
            return currentTime.until(broadcastTime, ChronoUnit.MILLIS);
        } else {
            return -1L;
        }
    }
}
