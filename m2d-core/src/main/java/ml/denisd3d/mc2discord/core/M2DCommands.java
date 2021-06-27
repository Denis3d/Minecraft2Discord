package ml.denisd3d.mc2discord.core;

import discord4j.common.util.TokenUtil;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class    M2DCommands {
    public static List<String> getStatus() {
        List<String> response = new ArrayList<>();

        response.add(LangManager.translate("commands.status.title"));
        response.add(LangManager.translate("commands.status.bot_name", Mc2Discord.INSTANCE.botName, Mc2Discord.INSTANCE.botDiscriminator));
        response.add(LangManager.translate("commands.status.bot_id", Mc2Discord.INSTANCE.getBotId()));
        response.add(LangManager.translate("commands.status.state", Mc2Discord.INSTANCE.getState()));
        for (int shard_id = 0; shard_id <= Mc2Discord.INSTANCE.client.getGatewayClientGroup().getShardCount(); shard_id++) {
            if (Mc2Discord.INSTANCE.client.getGatewayClientGroup().find(shard_id).isPresent()) {
                response.add(LangManager.translate("commands.status.shard",
                        shard_id,
                        Mc2Discord.INSTANCE.client.getGatewayClientGroup().find(shard_id).get().getResponseTime().toString()
                                .substring(2).replaceAll("(\\d[HMS])(?!$)", "$1 ")
                                .toLowerCase()));
            }

        }

        String newVersion = Mc2Discord.INSTANCE.iMinecraft.getNewVersion();
        if (!newVersion.isEmpty()) {
            response.add(LangManager.translate("commands.status.version", newVersion));
        }

        if (Mc2Discord.INSTANCE.errors.size() != 0) {
            response.add(LangManager.translate("commands.status.errors"));
            response.addAll(Mc2Discord.INSTANCE.errors);
        } else {
            response.add(LangManager.translate("commands.status.no_error"));
        }
        return response;
    }

    public static List<String> restart() {
        List<String> response = new ArrayList<>();
        Mc2Discord.INSTANCE.restart();
        response.add(LangManager.translate("commands.restart.content"));

        return response;
    }

    public static String[] upload() {
        try {
            HttpPost post = new HttpPost("https://m2d.denisd3d.ml/api/v1/upload/");

            String config = String.join("\n", Files.readAllLines(Mc2Discord.CONFIG_FILE.toPath()));

            String configWithoutToken = config.substring(0, config.indexOf("token = ")) +
                    "token = REMOVED|" + (M2DUtils.isTokenValid(Mc2Discord.INSTANCE.config.token) ? ("VALID|" + TokenUtil.getSelfId(Mc2Discord.INSTANCE.config.token)) : Mc2Discord.INSTANCE.config.token.isEmpty() ? "EMPTY" : "INVALID") +
                    config.substring(config.indexOf("\n", config.indexOf("oken = ")));

            // add request parameter, form parameters
            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("config", configWithoutToken));
            urlParameters.add(new BasicNameValuePair("errors", Mc2Discord.INSTANCE.errors.isEmpty() ? "None" : String.join("\n", Mc2Discord.INSTANCE.errors)));
            urlParameters.add(new BasicNameValuePair("env", Mc2Discord.INSTANCE.iMinecraft.getEnvInfo()));

            post.setEntity(new UrlEncodedFormEntity(urlParameters));

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                ResponseHandler<String> responseHandler = response -> {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity responseEntity = response.getEntity();
                        return responseEntity != null ? EntityUtils.toString(responseEntity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                };
                String responseBody = httpClient.execute(post, responseHandler);
                return new String[]{LangManager.translate("commands.upload.success"), responseBody};
            }
        } catch (Exception e) {
            Mc2Discord.logger.error(e);
            return new String[]{LangManager.translate("commands.upload.error"), ""};
        }
    }

    public static String getDiscordText() {
        return Mc2Discord.INSTANCE.config.discord_text;
    }

    public static String getDiscordLink() {
        return Mc2Discord.INSTANCE.config.discord_link;
    }

    public static String getInviteLink() {
        return M2DUtils.isTokenValid(Mc2Discord.INSTANCE.config.token) ? "https://discord.com/api/oauth2/authorize?client_id=" + TokenUtil.getSelfId(Mc2Discord.INSTANCE.config.token) + "&permissions=872926289&scope=bot" : LangManager.translate("commands.invite.error");
    }
}
