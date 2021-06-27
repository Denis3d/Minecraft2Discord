package ml.denisd3d.mc2discord.core.entities;

import java.util.HashMap;

public class Death extends Entity {

    public final String damageType;
    public final String message;
    public final int combatDuration;
    public final String attackerName;
    public final float attackerHealth;

    public HashMap<String, String> replacements = new HashMap<>();

    public Death(String damageType, String message, int combatDuration, String attackerName, float attackerHealth) {
        this.damageType = damageType;
        this.message = message.replaceAll("\u00A7", "");
        this.combatDuration = combatDuration;
        this.attackerName = attackerName;
        this.attackerHealth = attackerHealth;
    }

    @Override
    public String replace(String content) {
        replacements.put("damage_type", this.damageType);
        replacements.put("message", this.message);
        replacements.put("combat_duration", Integer.toString(this.combatDuration));
        replacements.put("attacker_name", this.attackerName);
        replacements.put("attacker_health", Float.toString(this.attackerHealth));

        return this.replace(content, "death", this.replacements);
    }
}
