package ml.denisd3d.mc2discord.core.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.conversion.PreserveNotNull;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import ml.denisd3d.mc2discord.core.LangManager;
import ml.denisd3d.mc2discord.core.M2DUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class M2DConfig {

    @Path("lang")
    @Comment("config.lang.comment")
    @PreserveNotNull
    public String lang = "en_us";

    //General
    @Path("General.token")
    @Comment("config.token.comment")
    @PreserveNotNull
    public String token = "";
    //End General

    //Channels
    @Path("Channels.Channel")
    @Comment("config.channels.comment")
    @PreserveNotNull
    public List<Channel> channels = new ArrayList<>();
    public transient HashMap<Long, Channel> channels_map = new HashMap<>();
    //End Channels

    //Messages
    @Path("Messages.start")
    @Comment("config.messages.start.comment")
    @Value("config.messages.start.value")
    @PreserveNotNull
    public String start_message;

    @Path("Messages.stop")
    @Comment("config.messages.stop.comment")
    @Value("config.messages.stop.value")
    @PreserveNotNull
    public String stop_message;

    @Path("Messages.join")
    @Comment("config.messages.join.comment")
    @Value("config.messages.join.value")
    @PreserveNotNull
    public String join_message;

    @Path("Messages.leave")
    @Comment("config.messages.leave.comment")
    @Value("config.messages.leave.value")
    @PreserveNotNull
    public String leave_message;

    @Path("Messages.death")
    @Comment("config.messages.death.comment")
    @Value("config.messages.death.value")
    @PreserveNotNull
    public String death_message;

    @Path("Messages.advancement")
    @Comment("config.messages.advancement.comment")
    @Value("config.messages.advancement.value")
    @PreserveNotNull
    public String advancement_message;
    //End Messages

    //Commands
    @Path("Commands.prefix")
    @Comment("config.commands.prefix.comment")
    @PreserveNotNull
    public String command_prefix = "/";

    @Path("Commands.use_codeblocks")
    @Comment("config.commands.use_codeblocks.comment")
    @PreserveNotNull
    public boolean use_codeblocks = true;

    @Path("Commands.error")
    @Comment("config.commands.error.comment")
    @Value("config.commands.error.value")
    @PreserveNotNull
    public String command_error_message;

    @Path("Commands.Command")
    @Comment("config.commands.command.comment")
    @PreserveNotNull
    public List<CommandRule> command_rules = new ArrayList<>();
    public transient HashMap<Long, CommandRule> command_rules_map = new HashMap<>();
    //End Commands

    //Status
    @Path("Status.Presence.message")
    @Comment("config.status.presence.message.comment")
    @Value("config.status.presence.message.value")
    @PreserveNotNull
    public String presence_message;

    @Path("Status.Presence.type")
    @Comment("config.status.presence.type.comment")
    @PreserveNotNull
    public String presence_type = "PLAYING";

    @Path("Status.Presence.update")
    @Comment("config.status.presence.update.comment")
    @PreserveNotNull
    public long presence_update = 60L;

    @Path("Status.Channels.Channel")
    @Comment("config.status.channels.channel.comment")
    @PreserveNotNull
    public List<StatusChannel> status_channels = new ArrayList<>();
    //End Status

    //Misc
    @Path("Misc.bot_name")
    @Comment("config.misc.bot_name.comment")
    @PreserveNotNull
    public String bot_name = "";

    @Path("Misc.bot_avatar")
    @Comment("config.misc.bot_avatar.comment")
    @PreserveNotNull
    public String bot_avatar = "";

    @Path("Misc.avatar_api")
    @Comment("config.misc.avatar_api.comment")
    @PreserveNotNull
    public String avatar_api = "https://mc-heads.net/head/${player_uuid}/right";

    @Path("Misc.relay_bot_messages")
    @Comment("config.misc.relay_bot_messages.comment")
    @PreserveNotNull
    public boolean relay_bot_messages = false;

    @Path("Misc.relay_say_me_command")
    @Comment("config.misc.relay_say_me_command.comment")
    @PreserveNotNull
    public boolean relay_say_me_command = true;

    @Path("Misc.allowed_mention")
    @Comment("config.misc.allowed_mention.comment")
    @PreserveNotNull
    public List<String> allowed_mention = new ArrayList<>();

    @Path("Misc.minecraft_chat_format")
    @Comment("config.misc.minecraft_chat_format.comment")
    @PreserveNotNull
    public String minecraft_chat_format = "<Discord - ${member_nickname}> ${message}";

    @Path("Misc.discord_chat_format")
    @Comment("config.misc.discord_chat_format.comment")
    @PreserveNotNull
    public String discord_chat_format = "**${player_display_name}**: ${message}";

    @Path("Misc.discord_text")
    @Comment("config.misc.discord_text.comment")
    @Value("config.misc.discord_text.value")
    @PreserveNotNull
    public String discord_text;

    @Path("Misc.discord_link")
    @Comment("config.misc.discord_link.comment")
    @PreserveNotNull
    public String discord_link = "https://discord.gg/";

    @Path("Misc.logs_format")
    @Comment("config.misc.logs_format.comment")
    @PreserveNotNull
    public String logs_format = "[${log_time!HH:mm:ss}] [${log_thread_name}/${log_level}] [${log_logger_name}]: ${log_message}";

    @Path("Misc.logs_level")
    @Comment("config.misc.logs_level.comment")
    @PreserveNotNull
    public String logs_level = "INFO";
    //End Misc

    public static M2DConfig load(File file, LangManager langManager) {
        Config.setInsertionOrderPreserved(true);

        ObjectConverter converter = new ObjectConverter();
        CommentedFileConfig config = CommentedFileConfig.of(file);
        config.load();

        M2DConfig m2dConfig = converter.toObject(config, M2DConfig::new);

        config.clear();
        if (!M2DUtils.available_lang.contains(m2dConfig.lang)) {
            m2dConfig.lang = "en_us";
        }

        if (m2dConfig.channels.isEmpty()) {
            m2dConfig.channels.add(new Channel());
        }

        for (Channel channel : m2dConfig.channels) {
            if (channel.subscriptions.isEmpty()) {
                channel.subscriptions.add("info");
                channel.subscriptions.add("chat");
                channel.subscriptions.add("command");
            }

            m2dConfig.channels_map.put(channel.channel_id, channel);
        }

        if (m2dConfig.command_rules.isEmpty()) {
            m2dConfig.command_rules.add(new CommandRule());
        }

        for (CommandRule entry : m2dConfig.command_rules) {
            if (entry.commands.isEmpty()) {
                entry.commands.add("help");
            }

            m2dConfig.command_rules_map.put(entry.id, entry);
        }

        if (m2dConfig.status_channels.isEmpty()) {
            m2dConfig.status_channels.add(new StatusChannel());
        }

        converter.toConfig(m2dConfig, config);

        config.setComment("General", langManager.formatMessage("config.general.comment"));

        config.setComment("Messages", langManager.formatMessage("config.messages.comment"));

        config.setComment("Misc", langManager.formatMessage("config.misc.comment"));

        boolean need_reload = false;
        for (Field field : m2dConfig.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Comment.class) && field.isAnnotationPresent(Path.class)) {

                config.setComment(field.getAnnotation(Path.class).value(), langManager.formatMessage(field.getAnnotation(Comment.class).value(),
                        field.getAnnotation(Comment.class).value().equals("config.lang.comment") ? String.join(", ", M2DUtils.available_lang) : null)
                );
            }
            if (field.isAnnotationPresent(Value.class) && field.isAnnotationPresent(Path.class) && config.get(field.getAnnotation(Path.class).value()) == null) {
                config.set(field.getAnnotation(Path.class).value(), langManager.formatMessage(field.getAnnotation(Value.class).value()));
                need_reload = true;
            }
        }

        if (need_reload)
            m2dConfig = converter.toObject(config, M2DConfig::new);
        config.save();

        return m2dConfig;
    }

    public static class Channel {
        @Path("id")
        @PreserveNotNull
        public long channel_id = 0L;

        @Path("subscriptions")
        @PreserveNotNull
        public List<String> subscriptions = new ArrayList<>();

        @Path("use_webhook")
        @PreserveNotNull
        public boolean use_webhook = true;
    }

    public static class CommandRule {
        @Path("id")
        @PreserveNotNull
        public long id = 0L;

        @Path("commands")
        @PreserveNotNull
        public List<String> commands = new ArrayList<>();

        @Path("permission_level")
        @PreserveNotNull
        public Integer permission_level = -1;

        @Path("comment")
        @PreserveNotNull
        public String comment = "";
    }

    public static class StatusChannel {
        @Path("id")
        @PreserveNotNull
        public long channel_id = 0L;

        @Path("update_period")
        @PreserveNotNull
        public long update_period = 610;

        @Path("name_message")
        @PreserveNotNull
        public String name_message = "${online_players} / ${max_players}";

        @Path("topic_message")
        @PreserveNotNull
        public String topic_message = "${online_players} / ${max_players}";
    }
}
