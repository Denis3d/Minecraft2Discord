package ml.denisd3d.mc2discord.forge.storage;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.management.UserListEntry;

import java.util.UUID;

public class HiddenPlayerEntry extends UserListEntry<GameProfile> {
    public HiddenPlayerEntry(GameProfile profile) {
        super(profile);
    }

    public HiddenPlayerEntry(JsonObject json) {
        super(gameProfileFromJsonObject(json));
    }

    private static GameProfile gameProfileFromJsonObject(JsonObject json) {
        if (json.has("uuid") && json.has("name")) {
            String s = json.get("uuid").getAsString();

            UUID uuid;
            try {
                uuid = UUID.fromString(s);
            } catch (Throwable throwable) {
                return null;
            }

            return new GameProfile(uuid, json.get("name").getAsString());
        } else {
            return null;
        }
    }

    @Override
    protected void onSerialization(JsonObject data) {
        if (this.getValue() != null) {
            data.addProperty("uuid", this.getValue().getId() == null ? "" : this.getValue().getId().toString());
            data.addProperty("name", this.getValue().getName());
        }
    }
}
