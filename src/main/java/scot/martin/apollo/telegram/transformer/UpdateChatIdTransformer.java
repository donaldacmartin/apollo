package scot.martin.apollo.telegram.transformer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;

public class UpdateChatIdTransformer implements Function<String, Set<Long>> {

    private static final Logger LOGGER = Logger.getLogger("UpdateCHatIdTransformer");

    @Override
    public Set<Long> apply(String respBody) {
        Set<Long> chatIds = new HashSet<>();

        if (respBody != null) {
            try {
                JSONObject jsonObject = new JSONObject(respBody);
                JSONArray results = jsonObject.getJSONArray("result");

                for (int i = 0; i < results.length(); i++) {
                    Optional<Long> chatId = getChatId(results.getJSONObject(i));

                    if (chatId.isPresent()) {
                        chatIds.add(chatId.get());
                    } else {
                        LOGGER.warning("No chat ID in message");
                    }
                }
            } catch (Exception e) {
                LOGGER.severe("Error parsing JSON: " + e.getMessage());
            }
        } else {
            LOGGER.warning("Cannot parse null JSON");
        }

        return chatIds;
    }

    private Optional<Long> getChatId(JSONObject result) {
        Optional<Long> chatId = Optional.empty();

        if (result.has("message")) {
            JSONObject message = result.getJSONObject("message");

            if (message.has("chat")) {
                JSONObject chat = message.getJSONObject("chat");

                if (chat.has("id")) {
                    chatId = Optional.of(chat.getLong("id"));
                }
            }
        }

        return chatId;
    }
}
