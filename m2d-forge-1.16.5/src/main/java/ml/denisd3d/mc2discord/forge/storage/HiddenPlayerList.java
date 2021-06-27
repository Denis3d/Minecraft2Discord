package ml.denisd3d.mc2discord.forge.storage;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.management.UserList;
import net.minecraft.server.management.UserListEntry;

import java.io.File;

public class HiddenPlayerList extends UserList<GameProfile, HiddenPlayerEntry> {

    public HiddenPlayerList(File saveFile) {
        super(saveFile);
    }

    protected UserListEntry<GameProfile> createEntry(JsonObject entryData) {
        return new HiddenPlayerEntry(entryData);
    }

    public boolean isHidden(GameProfile profile) {
        return this.hasEntry(profile);
    }

    @Override
    public String[] getKeys() {
        String[] astring = new String[this.getEntries().size()];
        int i = 0;

        for (UserListEntry<GameProfile> userlistentry : this.getEntries()) {
            astring[i++] = userlistentry.getValue().getName();
        }

        return astring;
    }

    @Override
    protected String getObjectKey(GameProfile obj) {
        return obj.getId().toString();
    }
}
