package ml.denisd3d.mc2discord.forge;

import ml.denisd3d.mc2discord.core.entities.Advancement;
import ml.denisd3d.mc2discord.core.entities.Death;
import ml.denisd3d.mc2discord.core.entities.Player;
import ml.denisd3d.mc2discord.core.events.MinecraftEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = "mc2discord", value = Dist.DEDICATED_SERVER, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Events {
    @SubscribeEvent
    public static void onMinecraftChatMessageEvent(ServerChatEvent event) {
        MinecraftEvents.onMinecraftChatMessageEvent(event.getMessage(), new Player(event.getPlayer().getGameProfile().getName(), event.getPlayer().getDisplayName().getString(), Optional.ofNullable(event.getPlayer().getGameProfile().getId()).orElse(null)));
    }

    @SubscribeEvent
    public static void onPlayerJoinEvent(PlayerEvent.PlayerLoggedInEvent event) {
        MinecraftEvents.onPlayerJoinEvent(new Player(event.getPlayer().getGameProfile().getName(), event.getPlayer().getDisplayName().getString(), Optional.ofNullable(event.getPlayer().getGameProfile().getId()).orElse(null)));
    }

    @SubscribeEvent
    public static void onPlayerLeaveEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        MinecraftEvents.onPlayerLeaveEvent(new Player(event.getPlayer().getGameProfile().getName(), event.getPlayer().getDisplayName().getString(), Optional.ofNullable(event.getPlayer().getGameProfile().getId()).orElse(null)));
    }

    @SubscribeEvent
    public static void onPlayerDieEvent(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            MinecraftEvents.onPlayerDieEvent(
                    new Player(player.getGameProfile().getName(), player.getDisplayName().getString(), Optional.ofNullable(player.getGameProfile().getId()).orElse(null)),
                    new Death(event.getSource().damageType, player.getCombatTracker().getDeathMessage().getString(), player.getCombatTracker().getCombatDuration(), Optional.ofNullable(player.getCombatTracker().getBestAttacker()).map(livingEntity -> livingEntity.getDisplayName().getString()).orElse(""), Optional.ofNullable(player.getCombatTracker().getBestAttacker()).map(LivingEntity::getHealth).orElse(0.0f)));
        }
    }

    @SubscribeEvent
    public static void onAdvancementEvent(AdvancementEvent event) {
        if (event.getAdvancement().getDisplay() != null && event.getAdvancement().getDisplay().shouldAnnounceToChat()) {
            MinecraftEvents.onAdvancementEvent(
                    new Player(event.getPlayer().getGameProfile().getName(), event.getPlayer().getDisplayName().getString(), Optional.ofNullable(event.getPlayer().getGameProfile().getId()).orElse(null)),
                    new Advancement(event.getAdvancement().getId().getPath(), event.getAdvancement().getDisplayText().getString(), event.getAdvancement().getDisplay().getTitle().getString(), event.getAdvancement().getDisplay().getDescription().getString())
            );
        }
    }
}
