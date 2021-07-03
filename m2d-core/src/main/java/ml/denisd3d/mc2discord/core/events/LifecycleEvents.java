package ml.denisd3d.mc2discord.core.events;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import ml.denisd3d.mc2discord.api.IM2DPlugin;
import ml.denisd3d.mc2discord.api.M2DPluginHelper;
import ml.denisd3d.mc2discord.core.M2DUtils;
import ml.denisd3d.mc2discord.core.Mc2Discord;
import ml.denisd3d.mc2discord.core.StatusManager;
import ml.denisd3d.mc2discord.core.entities.Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class LifecycleEvents {
    public static void onReady(ReadyEvent readyEvent) {
        Mc2Discord.INSTANCE.botName = readyEvent.getSelf().getUsername();
        Mc2Discord.INSTANCE.botDiscriminator = readyEvent.getSelf().getDiscriminator();
        Mc2Discord.INSTANCE.botId = readyEvent.getSelf().getId().asLong();
        Mc2Discord.INSTANCE.botDisplayName = Mc2Discord.INSTANCE.config.bot_name.isEmpty() ? readyEvent.getSelf().getUsername() : Entity.replace(Mc2Discord.INSTANCE.config.bot_name, Collections.emptyList());
        Mc2Discord.INSTANCE.botAvatar = Mc2Discord.INSTANCE.config.bot_avatar.isEmpty() ? readyEvent.getSelf().getAvatarUrl() : Entity.replace(Mc2Discord.INSTANCE.config.bot_avatar, Collections.emptyList());

        if (Mc2Discord.INSTANCE.config.channels.get(0).channel_id != 0) {
            ArrayList<Permission> requiredPermissions = new ArrayList<>(PermissionSet.of(604359761));

            Mc2Discord.INSTANCE.client
                    .getChannelById(Snowflake.of(Mc2Discord.INSTANCE.config.channels.get(0).channel_id))
                    .ofType(GuildChannel.class)
                    .flatMap(GuildChannel::getGuild)
                    .flatMap(guild -> Mc2Discord.INSTANCE.client.getSelfMember(guild.getId()))
                    .flatMap(Member::getBasePermissions)
                    .subscribe(permissions -> {
                        if (!permissions.contains(Permission.ADMINISTRATOR)) {
                            requiredPermissions.removeAll(permissions);
                            if (!requiredPermissions.isEmpty()) {
                                Mc2Discord.INSTANCE.errors.add(Mc2Discord.INSTANCE.langManager.formatMessage("errors.missing_permission", requiredPermissions.stream().map(Enum::name).collect(Collectors.joining(", "))));
                            }
                        }
                    });
        }

        Mc2Discord.logger.info("Discord bot connected as " + Mc2Discord.INSTANCE.botName);
        String newVersion = Mc2Discord.INSTANCE.iMinecraft.getNewVersion();
        if (!newVersion.isEmpty()) {
            Mc2Discord.logger.info("New version available: " + newVersion);
        }

        LifecycleEvents.bothReadyEvent();
    }

    public static void bothReadyEvent() {
        if (!(Mc2Discord.INSTANCE.isDiscordRunning() && Mc2Discord.INSTANCE.isMinecraftStarted()))
            return; // This method is called by both when ready. When the last is ready it will execute the rest of the method
        if (!M2DUtils.canHandleEvent())
            return;
        if (M2DPluginHelper.execute(IM2DPlugin::onReady))
            return;

        Mc2Discord.INSTANCE.startTime = System.currentTimeMillis();
        Mc2Discord.INSTANCE.messageManager.sendInfoMessage(Entity.replace(Mc2Discord.INSTANCE.config.start_message, Collections.emptyList()));
    }

    public static void onShutdown() {
        if (!M2DUtils.canHandleEvent())
            return;

        Mc2Discord.INSTANCE.is_stopping = true;

        if (M2DPluginHelper.execute(IM2DPlugin::onShutdown))
            return;

        Mc2Discord.INSTANCE.messageManager.sendInfoMessage(Entity.replace(Mc2Discord.INSTANCE.config.stop_message, Collections.emptyList()));
        StatusManager.stop();
    }
}
