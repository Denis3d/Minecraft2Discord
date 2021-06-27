package ml.denisd3d.mc2discord.core;

import ml.denisd3d.mc2discord.core.entities.Global;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface IMinecraft {
    void sendMessage(String content, HashMap<String, String> attachments);

    void executeCommand(String command, int permissionLevel, long messageChannelId, boolean useWebhook);

    Global getServerData();

    String executeHelpCommand(int permissionLevel, List<String> commands);

    boolean isPlayerHidden(UUID id, String name);

    String getNewVersion();

    String getEnvInfo();
}
