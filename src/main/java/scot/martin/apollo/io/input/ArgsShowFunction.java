package scot.martin.apollo.io.input;

import scot.martin.apollo.model.Show;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ArgsShowFunction implements Function<List<String>, Show> {

    private static final Logger LOGGER = Logger.getLogger("ArgShowFunction");

    @Override
    public Show apply(List<String> arguments) {
        Show show = null;

        if (arguments != null && arguments.size() == 12) {
            try {
                String name = arguments.get(0);
                LocalTime time = LocalTime.parse(arguments.get(1));
                ZoneId timeZone = ZoneId.of(arguments.get(2));

                List<DayOfWeek> daysOfWeek = List
                        .of(DayOfWeek.values())
                        .parallelStream()
                        .filter(d -> "Y".equalsIgnoreCase(arguments.get(3 + d.ordinal())))
                        .collect(Collectors.toList());

                long minutes = Long.parseLong(arguments.get(10));
                String url = arguments.get(11);

                show = new Show(name, time, timeZone, daysOfWeek, minutes, url);
                LOGGER.info("Parsed show " + show);
            } catch (Exception e) {
                LOGGER.warning("Error while parsing: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            LOGGER.warning("Bad arguments provided");
        }

        return show;
    }
}
