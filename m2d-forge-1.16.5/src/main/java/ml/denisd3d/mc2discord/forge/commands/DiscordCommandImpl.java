package ml.denisd3d.mc2discord.forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import ml.denisd3d.mc2discord.core.M2DCommands;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

public class DiscordCommandImpl {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("discord").executes(context -> {
            context.getSource().sendFeedback(
                    new StringTextComponent(M2DCommands.getDiscordText()).modifyStyle(style -> style
                            .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, M2DCommands.getDiscordLink()))
                            .setColor(Color.fromTextFormatting(TextFormatting.BLUE))
                            .setUnderlined(true)),
                    false);
            return 1;
        }));
    }
}
