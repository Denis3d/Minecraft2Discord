package ml.denisd3d.mc2discord.core;

import com.electronwill.nightconfig.core.io.ParsingException;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.gateway.GatewayObserver;
import ml.denisd3d.mc2discord.core.config.M2DConfig;
import ml.denisd3d.mc2discord.core.events.DiscordEvents;
import ml.denisd3d.mc2discord.core.events.LifecycleEvents;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import reactor.netty.ConnectionObserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Mc2Discord {
    public static final Logger logger = LogManager.getLogger("mc2discord");
    public static Mc2Discord INSTANCE;
    public static File CONFIG_FILE = new File("config", "mc2discord.toml");
    public final LangManager langManager;
    public boolean is_stopping;
    public M2DConfig config;
    public List<String> errors = new ArrayList<>();
    public GatewayDiscordClient client;
    public long botId = -1;
    public String botName = "Undefined";
    public String botDiscriminator = "0000";
    public String botAvatar;
    public String botDisplayName;
    public IMinecraft iMinecraft;
    public MessageManager messageManager = new MessageManager(this);
    public long startTime;
    private ConnectionObserver.State state = GatewayObserver.DISCONNECTED;
    private boolean isMinecraftStarted;

    public Mc2Discord(boolean minecraftReady, IMinecraft iMinecraft) {
        this.isMinecraftStarted = minecraftReady;
        this.iMinecraft = iMinecraft;

        String lang = "en_us";
        try {
            if (CONFIG_FILE.exists()) {
                Scanner scanner = new Scanner(CONFIG_FILE);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    int index = line.indexOf("lang = ");
                    if (index != -1) {
                        lang = line.substring(index + 8, index + 13); // get 4 letter
                        break;
                    }
                }
                scanner.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (!M2DUtils.available_lang.contains(lang))
            lang = "en_us";
        langManager = new LangManager(lang);

        try {
            this.config = M2DConfig.load(CONFIG_FILE, langManager);
        } catch (ParsingException parsingException) {
            this.errors.add(langManager.formatMessage("errors.config_parsing"));
            parsingException.printStackTrace();
            this.config = null;
            return;
        }

        if (!M2DUtils.isTokenValid(this.config.token)) {
            logger.error("Invalid Discord bot token");
            this.errors.add(langManager.formatMessage("errors.invalid_token"));
            return;
        }

        Thread thread = new Thread(() -> {
            this.client = DiscordClientBuilder.create(this.config.token).build().gateway().setGatewayObserver((newState, client) -> {
                if (newState == GatewayObserver.RETRY_SUCCEEDED) {
                    state = GatewayObserver.CONNECTED;
                } else if (newState != GatewayObserver.SEQUENCE) {
                    state = newState;
                }
            })
                    .login().doOnError(throwable -> errors.add(throwable.getLocalizedMessage()))
                    .block();

            if (this.client == null) {
                this.errors.add(langManager.formatMessage("errors.login"));
                return;
            }

            this.registerEvents(client.getEventDispatcher());
            this.client.onDisconnect().block();
            StatusManager.stop();
        });
        thread.setName("Mc2Discord");
        thread.setDaemon(true);
        thread.start();
    }

    public static void firstInit() {
        Configurator.setLevel("discord4j.rest", Level.INFO);
        DiscordLogging.init();
    }

    public void registerEvents(EventDispatcher eventDispatcher) {
        eventDispatcher.on(ReadyEvent.class).subscribe(LifecycleEvents::onReady);
        eventDispatcher.on(MessageCreateEvent.class).subscribe(DiscordEvents::onDiscordMessageReceived);
        StatusManager.register();
        DiscordLogging.logs = ""; // Clear pending logs on restart
    }

    public boolean isDiscordRunning() {
        return this.state == GatewayObserver.CONNECTED;
    }

    public ConnectionObserver.State getState() {
        return state;
    }

    public boolean isMinecraftStarted() {
        return isMinecraftStarted;
    }

    public void setMinecraftStarted(boolean minecraftStarted) {
        isMinecraftStarted = minecraftStarted;
    }

    public long getBotId() {
        return this.botId;
    }

    public void restart() {
        if (client != null) {
            this.client.logout().subscribe(null, null, () -> Mc2Discord.INSTANCE = new Mc2Discord(true, this.iMinecraft));
        } else {
            Mc2Discord.INSTANCE = new Mc2Discord(true, this.iMinecraft);
        }
    }
}
