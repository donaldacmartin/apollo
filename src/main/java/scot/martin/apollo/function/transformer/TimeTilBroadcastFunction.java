package scot.martin.apollo.function.transformer;

import scot.martin.apollo.model.Show;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

public class TimeTilBroadcastFunction implements Function<Show, Long> {

    private final Function<ZoneId, LocalTime> currentLocalTimeFunction;

    public TimeTilBroadcastFunction() {
        this.currentLocalTimeFunction = tz -> ZonedDateTime.now(tz).toLocalTime();
    }

    @Override
    public Long apply(Show show) {
        LocalTime broadcastTime = show.getTime();
        ZoneId timeZone = show.getTimeZone();
        LocalTime currentTime = currentLocalTimeFunction.apply(timeZone);

        return currentTime.until(broadcastTime, ChronoUnit.MILLIS);
    }
}
