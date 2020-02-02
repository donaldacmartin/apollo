package scot.martin.apollo.telegram.io;

import scot.martin.apollo.telegram.transformer.TelegramRequestFunction;
import scot.martin.apollo.telegram.transformer.UpdateChatIdTransformer;
import scot.martin.apollo.telegram.transformer.UpdateLastIdTransformer;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

public final class TelegramDao implements Supplier<Set<Long>>,
        BiConsumer<Set<Long>, String> {

    private static final Logger LOGGER = Logger.getLogger("TelegramDao");
    private static final int TIMEOUT_SECS = 5;
    private static final String PARSE_MODE = "markdown";
    private static final long DEFAULT_LAST_UPDATE = 0;

    private final Function<String, Optional<String>> telegramRequestFunction;
    private final Function<String, Set<Long>> updateChatIdTransformer;
    private final Function<String, OptionalLong> updateLastIdTransformer;

    private Long lastUpdate;

    public TelegramDao() {
        this.telegramRequestFunction = new TelegramRequestFunction();
        this.updateChatIdTransformer = new UpdateChatIdTransformer();
        this.updateLastIdTransformer = new UpdateLastIdTransformer();
        this.lastUpdate = 0L;
    }

    @Override
    public Set<Long> get() {
        Set<Long> chatIds = new HashSet<>();

        StringBuilder qry = new StringBuilder()
                .append("getUpdates")
                .append("?offset=")
                .append(lastUpdate + 1)
                .append("&timeout=")
                .append(TIMEOUT_SECS);

        LOGGER.info("Calling Telegram for chat IDs");

        Optional<String> response = telegramRequestFunction.apply(qry.toString());

        if (response.isPresent()) {
            LOGGER.info("Got update");

            String body = response.get();
            chatIds = updateChatIdTransformer.apply(body);
            lastUpdate = updateLastIdTransformer.apply(body).orElse(DEFAULT_LAST_UPDATE);
        } else {
            LOGGER.warning("No response from Telegram");
        }

        return chatIds;
    }

    @Override
    public void accept(Set<Long> chatIds, String msg) {
        String urlMsg = URLEncoder.encode(msg, Charset.defaultCharset());

        Function<Long, String> chatIdToReq = c ->
                new StringBuilder()
                        .append("sendMessage")
                        .append("?chat_id=")
                        .append(c)
                        .append("&text=")
                        .append(urlMsg)
                        .append("&parse_mode=")
                        .append(PARSE_MODE)
                        .toString();

        LOGGER.info("Sending messages");
        Consumer<String> sendMsg = m -> telegramRequestFunction.apply(m);

        if (chatIds != null) {
            chatIds.stream().map(chatIdToReq).forEach(sendMsg);
        }
    }
}
