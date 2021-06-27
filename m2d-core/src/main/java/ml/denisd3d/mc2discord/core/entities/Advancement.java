package ml.denisd3d.mc2discord.core.entities;

import java.util.HashMap;

public class Advancement extends Entity {
    private final String path;
    private final String displayText;
    private final String title;
    private final String description;

    public HashMap<String, String> replacements = new HashMap<>();

    public Advancement(String path, String displayText, String title, String description) {

        this.path = path;
        this.displayText = displayText;
        this.title = title;
        this.description = description;
    }

    @Override
    String replace(String content) {
        replacements.put("path", this.path);
        replacements.put("display_text", this.displayText);
        replacements.put("title", this.title);
        replacements.put("description", this.description);
        return replace(content, "advancement", replacements);
    }
}
