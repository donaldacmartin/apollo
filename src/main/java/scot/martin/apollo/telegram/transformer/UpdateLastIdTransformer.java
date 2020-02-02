package scot.martin.apollo.telegram.transformer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;

public class UpdateLastIdTransformer implements Function<String, OptionalLong> {

    private static final Logger LOGGER = Logger.getLogger("UpdateLastIdTransformer");

    @Override
    public OptionalLong apply(String msg) {
        Set<Long> updateIds = new HashSet<>();

        if (msg != null) {
            try {
                JSONObject jsonObject = new JSONObject(msg);
                JSONArray results = jsonObject.getJSONArray("result");

                for (int i = 0; i < results.length(); i++) {
                    Optional<Long> updateId = getUpdateId(results.getJSONObject(i));

                    if (updateId.isPresent()) {
                        updateIds.add(updateId.get());
                    } else {
                        LOGGER.warning("No update ID in message");
                    }
                }
            } catch (Exception e) {
                LOGGER.severe("Error parsing JSON: " + e.getMessage());
            }
        } else {
            LOGGER.warning("Cannot parse null JSON");
        }

        return updateIds.stream().mapToLong(Long::longValue).max();
    }

    private Optional<Long> getUpdateId(JSONObject result) {
        Optional<Long> updateId = Optional.empty();

        if (result.has("update_id")) {
            updateId = Optional.of(result.getLong("update_id"));

        }

        return updateId;
    }
}
