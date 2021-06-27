package ml.denisd3d.mc2discord.api;

import discord4j.core.event.domain.message.MessageCreateEvent;
import ml.denisd3d.mc2discord.core.entities.Advancement;
import ml.denisd3d.mc2discord.core.entities.Death;
import ml.denisd3d.mc2discord.core.entities.Player;

public interface IM2DPlugin {
    boolean onReady();

    boolean onShutdown();

    boolean onDiscordMessageReceived(MessageCreateEvent messageCreateEvent);

    boolean onMinecraftChatMessageEvent(String message, Player player);

    boolean onPlayerJoinEvent(Player player);

    boolean onPlayerLeaveEvent(Player player);

    boolean onPlayerDieEvent(Player player, Death death);

    boolean onAdvancementEvent(Player player, Advancement advancement);
}
