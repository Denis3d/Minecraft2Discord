package ml.denisd3d.mc2discord.core.entities;

import java.util.HashMap;

public class Global extends Entity {
    public final int onlinePlayers;
    public final int maxPlayers;
    public final int uniquePlayers;
    public final String motd;
    public final String mcVersion;
    public final String serverHostname;
    public final String serverPort;
    public final String now;
    public final String uptime;

    public HashMap<String, String> replacements = new HashMap<>();

    public Global(int onlinePlayers, int maxPlayers, int uniquePlayers, String motd, String mcVersion, String serverHostname, String serverPort, String now, String uptime) {
        this.onlinePlayers = onlinePlayers;
        this.maxPlayers = maxPlayers;
        this.uniquePlayers = uniquePlayers;
        this.motd = motd;
        this.mcVersion = mcVersion;
        this.serverHostname = serverHostname;
        this.serverPort = serverPort;
        this.now = now;
        this.uptime = uptime;
    }

    @Override
    public String replace(String content) {
        replacements.put("online_players", String.valueOf(this.onlinePlayers));
        replacements.put("max_players", String.valueOf(this.maxPlayers));
        replacements.put("unique_players", String.valueOf(this.uniquePlayers));
        replacements.put("motd", this.motd);
        replacements.put("mc_version", this.mcVersion);
        replacements.put("server_hostname", this.serverHostname);
        replacements.put("server_port", this.serverPort);
        replacements.put("now", this.now);
        replacements.put("uptime", this.uptime);
        return replace(content, null, replacements);
    }
}
