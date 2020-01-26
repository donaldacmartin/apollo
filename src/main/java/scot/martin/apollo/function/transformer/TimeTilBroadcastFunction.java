package scot.martin.apollo.function.transformer;

import scot.martin.apollo.model.Show;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

public class TimeTilBroadcastFunction implements Function<Show, Long> {

    private final Function<ZoneId, LocalDateTime> currentLocalDateTimeFunction;

    public TimeTilBroadcastFunction() {
        this.currentLocalDateTimeFunction = tz -> ZonedDateTime.now(tz).toLocalDateTime();
    }

    @Override
    public Long apply(Show show) {
        LocalTime broadcastTime = show.getTime();
        ZoneId timeZone = show.getTimeZone();
        LocalDateTime currentDateTime = currentLocalDateTimeFunction.apply(timeZone);

        DayOfWeek dayOfWeek = currentDateTime.getDayOfWeek();
        LocalTime currentTime = currentDateTime.toLocalTime();

        if (show.getDaysOfWeek().contains(dayOfWeek)) {
            return currentTime.until(broadcastTime, ChronoUnit.MILLIS);
        } else {
            return -1l;
        }
    }
}
