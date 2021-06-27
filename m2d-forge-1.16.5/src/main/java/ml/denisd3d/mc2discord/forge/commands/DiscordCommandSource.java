package ml.denisd3d.mc2discord.forge.commands;

import ml.denisd3d.mc2discord.core.Mc2Discord;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.text.ITextComponent;

import java.util.UUID;

public class DiscordCommandSource implements ICommandSource {
    public static String answer = ""; // This is a hack to have only one string as result for the command. We need to clear it after each use and get the result by our self
    public static long messageChannelId;
    public static boolean useWebhook;
    private long time;
    private Thread messageScheduler;

    @Override
    public void sendMessage(ITextComponent component, UUID p_145747_2_) {
        answer += component.getString() + "\n";
        scheduleMessage();
    }

    @Override
    public boolean shouldReceiveFeedback() {
        return true;
    }

    @Override
    public boolean shouldReceiveErrors() {
        return true;
    }

    @Override
    public boolean allowLogging() {
        return true;
    }

    private void scheduleMessage() {
        time = System.currentTimeMillis();
        if (messageScheduler == null || !messageScheduler.isAlive()) {
            messageScheduler = new Thread(() -> {
                while (true) {
                    if (System.currentTimeMillis() - time > 50) {
                        Mc2Discord.INSTANCE.messageManager.sendMessageInChannel(messageChannelId, answer, useWebhook, Mc2Discord.INSTANCE.config.use_codeblocks, null);

                        answer = "";
                        break;
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Mc2Discord.logger.error(e);
                    }
                }
            });
            messageScheduler.start();
        }
    }
}
