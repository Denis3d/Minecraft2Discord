package ml.denisd3d.mc2discord.core.entities;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.message.Message;

import java.util.HashMap;

public class Log extends Entity {
    public final String loggerName;
    public final String threadName;
    public final long time;
    public final Level level;
    public final Message message;

    public HashMap<String, String> replacements = new HashMap<>();

    public Log(String loggerName, String threadName, long time, Level level, Message message) {

        this.loggerName = loggerName;
        this.threadName = threadName;
        this.time = time;
        this.level = level;
        this.message = message;
    }

    @Override
    public String replace(String content) {
        replacements.put("logger_name", this.loggerName.substring(this.loggerName.lastIndexOf(".") + 1));
        replacements.put("thread_name", this.threadName);
        replacements.put("time", String.valueOf(this.time));
        replacements.put("level", this.level.name());
        replacements.put("message", this.message.getFormattedMessage());
        return this.replace(content, "log", this.replacements);
    }
}
