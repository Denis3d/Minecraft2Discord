package ml.denisd3d.mc2discord.core.entities;

import ml.denisd3d.mc2discord.core.Mc2Discord;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import reactor.util.annotation.Nullable;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Entity {
    Pattern pattern = Pattern.compile("\\$\\{(.*?)}");

    public static String replace(String content, List<Entity> entities) {
        content = Mc2Discord.INSTANCE.iMinecraft.getServerData().replace(content);
        for (Entity entity : entities) {
            content = entity.replace(content);
        }
        return content;
    }

    abstract String replace(String content);

    String replace(String content, @Nullable String prefix, Map<String, String> replacements) {
        Matcher matcher = pattern.matcher(content);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String replacement;
            if (matcher.group(1).contains("!")) {
                int separator = matcher.group(1).indexOf("!");
                String value = replacements.get(matcher.group(1).substring(prefix != null ? prefix.length() + 1 : 0, separator));
                if (value != null) {
                    replacement = DateFormatUtils.format(Long.parseLong(value), matcher.group(1).substring(separator + 1));
                } else {
                    replacement = null;
                }

            } else if (matcher.group(1).contains("@")) {
                int separator = matcher.group(1).indexOf("@");
                String value = replacements.get(matcher.group(1).substring(prefix != null ? prefix.length() + 1 : 0, separator));
                if (value != null) {
                    replacement = DurationFormatUtils.formatDuration(Long.parseLong(value), matcher.group(1).substring(separator + 1));
                } else {
                    replacement = null;
                }
            } else {
                replacement = replacements.get(matcher.group(1).substring(prefix != null ? prefix.length() + 1 : 0));
            }

            if (replacement != null) {
                matcher.appendReplacement(buffer, "");
                buffer.append(replacement);
            }

        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
