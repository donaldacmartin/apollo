package scot.martin.apollo.function.transformer;

import scot.martin.apollo.model.Show;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

public class ArgsShowFunction implements Function<List<String>, Show> {

    private static final Logger LOGGER = Logger.getLogger("ArgShowFunction");

    @Override
    public Show apply(List<String> arguments) {
        Show show = null;

        if (arguments != null && arguments.size() == 5) {
            try {
                String name = arguments.get(0);
                LocalTime time = LocalTime.parse(arguments.get(1));
                ZoneId timeZone = ZoneId.of(arguments.get(2));
                long minutes = Long.parseLong(arguments.get(3));
                String url = arguments.get(4);

                show = new Show(name, time, timeZone, minutes, url);
                LOGGER.info("Parsed show " + show);
            } catch (Exception e) {
                LOGGER.warning("Error while parsing: " + e.getMessage());
            }
        } else {
            LOGGER.warning("Bad arguments provided");
        }

        return show;
    }
}
