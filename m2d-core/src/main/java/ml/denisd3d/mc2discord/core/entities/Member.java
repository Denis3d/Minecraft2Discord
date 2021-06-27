package ml.denisd3d.mc2discord.core.entities;

import java.util.HashMap;

public class Member extends Entity {
    public final String name;
    public final String discriminator;
    public final String nickname;
    public final String avatarUrl;

    public HashMap<String, String> replacements = new HashMap<>();

    public Member(String name, String discriminator, String nickname, String avatarUrl) {
        this.name = name;
        this.discriminator = discriminator;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String replace(String content) {
        replacements.put("name", this.name);
        replacements.put("discriminator", this.discriminator);
        replacements.put("tag", this.name + "#" + this.discriminator);
        replacements.put("nickname", this.nickname);
        replacements.put("avatar_url", this.avatarUrl);
        return this.replace(content, "member", this.replacements);
    }
}
