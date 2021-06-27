package ml.denisd3d.mc2discord.forge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import ml.denisd3d.mc2discord.forge.Mc2DiscordForge;
import net.minecraft.command.CommandSource;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Map;

public class HelpCommandImpl {
    public static String execute(int permissionLevel, List<String> commands) {
        CommandDispatcher<CommandSource> commandDispatcher = ServerLifecycleHooks.getCurrentServer().getCommandManager().getDispatcher();

        StringBuilder response = new StringBuilder();

        if (permissionLevel >= 0) {
            Map<CommandNode<CommandSource>, String> map = commandDispatcher.getSmartUsage(commandDispatcher.getRoot(), Mc2DiscordForge.commandSource.withPermissionLevel(permissionLevel));

            for (String string : map.values()) {
                response.append("/").append(string).append("\n");
            }
        }

        for (String command : commands) {
            commandDispatcher.getSmartUsage(commandDispatcher.getRoot().getChild(command.split(" ")[0]), Mc2DiscordForge.commandSource).values().forEach(s -> response.append("/").append(s).append("\n"));
        }

        return response.toString();
    }
}
