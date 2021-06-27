package ml.denisd3d.mc2discord.core.entities;

import com.vdurmont.emoji.EmojiParser;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message extends Entity {
    private static final Pattern p = Pattern.compile(" <:(.+?):\\d+>");
    public String message;
    public HashMap<String, String> replacements = new HashMap<>();

    public Message(String message) {
        this.message = message;
    }

    @Override
    public String replace(String content) {
        Matcher m = p.matcher(message);
        if (m.find()) {
            message = m.replaceFirst(":$1:");
        }
        replacements.put("message", EmojiParser.parseToAliases(this.message));
        return replace(content, null, replacements);
    }
}
