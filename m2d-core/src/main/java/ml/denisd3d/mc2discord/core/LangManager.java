package ml.denisd3d.mc2discord.core;

import com.google.gson.*;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.regex.Pattern;

public class LangManager {

    private static final Gson GSON = new Gson();
    private static final Pattern PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    protected final Map<String, String> properties = new HashMap<>();

    public LangManager(String lang) {
        String s = String.format("/assets/mc2discord/m2d-lang/core/%s.json", lang);
        try {
            InputStream inputStream = LangManager.class.getResourceAsStream(s);
            this.loadLocaleData(inputStream);
        } catch (Exception exception) {
            Mc2Discord.logger.warn("Skipped Minecraft2Discord language file: {} ({})", s, exception.toString());
        }
    }

    public static JsonObject getJsonObject(JsonElement json, String memberName) {
        if (json.isJsonObject()) {
            return json.getAsJsonObject();
        } else {
            throw new JsonSyntaxException("Expected " + memberName + " to be a JsonObject, was " + toString(json));
        }
    }

    public static String getString(JsonElement json, String memberName) {
        if (json.isJsonPrimitive()) {
            return json.getAsString();
        } else {
            throw new JsonSyntaxException("Expected " + memberName + " to be a string, was " + toString(json));
        }
    }

    public static String toString(JsonElement json) {
        String s = org.apache.commons.lang3.StringUtils.abbreviateMiddle(String.valueOf(json), "...", 10);
        if (json == null) {
            return "null (missing)";
        } else if (json.isJsonNull()) {
            return "null (json)";
        } else if (json.isJsonArray()) {
            return "an array (" + s + ")";
        } else if (json.isJsonObject()) {
            return "an object (" + s + ")";
        } else {
            if (json.isJsonPrimitive()) {
                JsonPrimitive jsonprimitive = json.getAsJsonPrimitive();
                if (jsonprimitive.isNumber()) {
                    return "a number (" + s + ")";
                }

                if (jsonprimitive.isBoolean()) {
                    return "a boolean (" + s + ")";
                }
            }

            return s;
        }
    }

    private void loadLocaleData(InputStream inputStreamIn) {
        try {
            JsonElement jsonelement = GSON.fromJson(new InputStreamReader(inputStreamIn, StandardCharsets.UTF_8), JsonElement.class);
            JsonObject jsonobject = getJsonObject(jsonelement, "strings");

            for (Map.Entry<String, JsonElement> entry : jsonobject.entrySet()) {
                String s = PATTERN.matcher(getString(entry.getValue(), entry.getKey())).replaceAll("%$1s");
                this.properties.put(entry.getKey(), s);
            }
        } finally {
            IOUtils.closeQuietly(inputStreamIn);
        }
    }

    private String translateKeyPrivate(String translateKey) {
        String s = this.properties.get(translateKey);
        return s == null ? translateKey : s;
    }

    public String formatMessage(String translateKey, Object... parameters) {
        String s = this.translateKeyPrivate(translateKey);

        try {
            return String.format(s, parameters);
        } catch (IllegalFormatException var5) {
            return "Format error: " + s;
        }
    }

    public static String translate(String translateKey, Object... parameters) {
        return Mc2Discord.INSTANCE.langManager.formatMessage(translateKey, parameters);
    }
}
