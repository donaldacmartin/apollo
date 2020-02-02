package scot.martin.apollo.telegram.transformer;

import scot.martin.apollo.model.BroadcastDownload;

import java.util.function.Function;

public class DownloadMsgFunction implements Function<BroadcastDownload, String> {

    @Override
    public String apply(BroadcastDownload broadcast) {
        StringBuilder msg = new StringBuilder()
                .append("Téléchargement pour ")
                .append(broadcast.getName())
                .append(" disponible [ici](")
                .append(broadcast.getUrl())
                .append(").");

        return msg.toString();
    }
}
