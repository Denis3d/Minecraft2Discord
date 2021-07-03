package ml.denisd3d.mc2discord.core;

import discord4j.gateway.GatewayObserver;
import ml.denisd3d.mc2discord.core.entities.Entity;
import ml.denisd3d.mc2discord.core.entities.Log;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.util.Collections;

@Plugin(name = "DiscordLogging", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class DiscordLogging extends AbstractAppender {
    public static String logs = "";
    private long time;
    private Thread messageScheduler;

    protected DiscordLogging(String name, Filter filter) {
        super(name, filter, null, true, Property.EMPTY_ARRAY);
    }

    @PluginFactory
    public static DiscordLogging createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Filter") Filter filter) {
        return new DiscordLogging(name, filter);
    }

    public static void init() {
        DiscordLogging da = DiscordLogging.createAppender("DiscordLogging", null);
        ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addAppender(da);
        da.start();
    }

    @Override
    public void append(LogEvent event) {
        if (Mc2Discord.INSTANCE != null && Mc2Discord.INSTANCE.client != null && Mc2Discord.INSTANCE.getState() == GatewayObserver.CONNECTED && !Mc2Discord.INSTANCE.is_stopping && event.getLevel().intLevel() <= Level.getLevel(Mc2Discord.INSTANCE.config.logs_level).intLevel()) {
            logs += Entity.replace(Mc2Discord.INSTANCE.config.logs_format, Collections.singletonList(new Log(event.getLoggerName(), event.getThreadName(), event.getInstant().getEpochMillisecond(), event.getLevel(), event.getMessage()))) + "\n";
            scheduleMessage();
        }
    }

    private void scheduleMessage() {
        time = System.currentTimeMillis();
        if (messageScheduler == null || !messageScheduler.isAlive()) {
            messageScheduler = new Thread(() -> {
                while (true) {
                    if (Mc2Discord.INSTANCE.is_stopping)
                        return;
                    if (System.currentTimeMillis() - time > 50) {
                        if (M2DUtils.canHandleEvent())
                            Mc2Discord.INSTANCE.messageManager.sendMessageOfType("log", logs, "", Mc2Discord.INSTANCE.botDisplayName, Mc2Discord.INSTANCE.botAvatar, null);

                        logs = "";
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