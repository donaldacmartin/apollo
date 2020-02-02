package scot.martin.apollo.model;

import java.util.Objects;

public class BroadcastDownload {

    private final String name;
    private final String url;

    public BroadcastDownload(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BroadcastDownload)) return false;
        BroadcastDownload that = (BroadcastDownload) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url);
    }
}
