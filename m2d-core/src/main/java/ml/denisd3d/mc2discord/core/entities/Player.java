package ml.denisd3d.mc2discord.core.entities;

import java.util.HashMap;
import java.util.UUID;

public class Player extends Entity {
    public final String name;
    public final String displayName;
    public final UUID uuid;

    public HashMap<String, String> replacements = new HashMap<>();

    public Player(String name, String displayName, UUID uuid) {
        this.name = name;
        this.displayName = displayName.replaceAll("\u00A7.", "");
        this.uuid = uuid;
    }

    @Override
    public String replace(String content) {
        replacements.put("name", this.name);
        replacements.put("display_name", this.displayName);
        replacements.put("uuid", this.uuid != null ? this.uuid.toString() : "");
        return this.replace(content, "player", this.replacements);
    }
}
