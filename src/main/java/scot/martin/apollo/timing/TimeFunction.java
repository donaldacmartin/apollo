package scot.martin.apollo.timing;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.Function;

public class TimeFunction implements Function<Long, LocalDateTime> {

    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    @Override
    public LocalDateTime apply(Long epochSeconds) {
        Instant instant = Instant.ofEpochMilli(epochSeconds);
        return instant.atZone(ZONE_ID).toLocalDateTime();
    }
}
