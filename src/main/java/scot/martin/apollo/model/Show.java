package scot.martin.apollo.model;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

public class Show implements Serializable {

    private static final long serialVersionUID = 2429520622449885838L;

    private final String name;
    private final LocalTime time;
    private final ZoneId timeZone;
    private final List<DayOfWeek> daysOfWeek;
    private final long minutes;
    private final String url;

    public Show(String name, LocalTime time, ZoneId timeZone,
                List<DayOfWeek> daysOfWeek, long mins, String url) {
        this.name = name;
        this.time = time;
        this.timeZone = timeZone;
        this.daysOfWeek = daysOfWeek;
        this.minutes = mins;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public LocalTime getTime() {
        return time;
    }

    public ZoneId getTimeZone() {
        return timeZone;
    }

    public List<DayOfWeek> getDaysOfWeek() {
        return daysOfWeek;
    }

    public long getMinutes() {
        return minutes;
    }

    public long getMillis() {
        return minutes * 60 * 1000;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Show)) return false;
        Show show = (Show) o;
        return minutes == show.minutes &&
                Objects.equals(name, show.name) &&
                Objects.equals(time, show.time) &&
                Objects.equals(timeZone, show.timeZone) &&
                Objects.equals(daysOfWeek, show.daysOfWeek) &&
                Objects.equals(url, show.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, time, timeZone, daysOfWeek, minutes, url);
    }

    @Override
    public String toString() {
        return "Show{" +
                "name='" + name + '\'' +
                ", time=" + time +
                ", timeZone=" + timeZone +
                ", daysOfWeek=" + daysOfWeek +
                ", minutes=" + minutes +
                ", url='" + url + '\'' +
                '}';
    }
}
