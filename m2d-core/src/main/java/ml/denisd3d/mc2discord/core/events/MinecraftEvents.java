package ml.denisd3d.mc2discord.core.events;

import ml.denisd3d.mc2discord.api.M2DPluginHelper;
import ml.denisd3d.mc2discord.core.M2DUtils;
import ml.denisd3d.mc2discord.core.Mc2Discord;
import ml.denisd3d.mc2discord.core.entities.*;

import java.util.Arrays;
import java.util.Collections;

public class MinecraftEvents {
    public static void onMinecraftChatMessageEvent(String message, Player player) {
        if (M2DPluginHelper.execute(plugin -> plugin.onMinecraftChatMessageEvent(message, player)))
            return;

        if (!M2DUtils.canHandleEvent() || Mc2Discord.INSTANCE.iMinecraft.isPlayerHidden(player.uuid, player.name))
            return;
        Mc2Discord.INSTANCE.messageManager.sendChatMessage(message, Entity.replace(Mc2Discord.INSTANCE.config.discord_chat_format, Arrays.asList(player, new Message(message))),
                player.displayName,
                Entity.replace(Mc2Discord.INSTANCE.config.avatar_api, Collections.singletonList(player)));
    }

    public static void onPlayerJoinEvent(Player player) {
        if (M2DPluginHelper.execute(plugin -> plugin.onPlayerJoinEvent(player)))
            return;

        if (!M2DUtils.canHandleEvent() || Mc2Discord.INSTANCE.iMinecraft.isPlayerHidden(player.uuid, player.name))
            return;
        Mc2Discord.INSTANCE.messageManager.sendInfoMessage(Entity.replace(Mc2Discord.INSTANCE.config.join_message, Collections.singletonList(player)));
    }

    public static void onPlayerLeaveEvent(Player player) {
        if (M2DPluginHelper.execute(plugin -> plugin.onPlayerLeaveEvent(player)))
            return;

        if (!M2DUtils.canHandleEvent() || Mc2Discord.INSTANCE.iMinecraft.isPlayerHidden(player.uuid, player.name))
            return;
        Mc2Discord.INSTANCE.messageManager.sendInfoMessage(Entity.replace(Mc2Discord.INSTANCE.config.leave_message, Collections.singletonList(player)));
    }

    public static void onPlayerDieEvent(Player player, Death death) {
        if (M2DPluginHelper.execute(plugin -> plugin.onPlayerDieEvent(player, death)))
            return;

        if (!M2DUtils.canHandleEvent() || Mc2Discord.INSTANCE.iMinecraft.isPlayerHidden(player.uuid, player.name))
            return;
        Mc2Discord.INSTANCE.messageManager.sendInfoMessage(Entity.replace(Mc2Discord.INSTANCE.config.death_message, Arrays.asList(player, death)));
    }

    public static void onAdvancementEvent(Player player, Advancement advancement) {
        if (M2DPluginHelper.execute(plugin -> plugin.onAdvancementEvent(player, advancement)))
            return;

        if (!M2DUtils.canHandleEvent() || Mc2Discord.INSTANCE.iMinecraft.isPlayerHidden(player.uuid, player.name))
            return;
        Mc2Discord.INSTANCE.messageManager.sendInfoMessage(Entity.replace(Mc2Discord.INSTANCE.config.advancement_message, Arrays.asList(player, advancement)));
    }
}
