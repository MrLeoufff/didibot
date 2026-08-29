package fr.dwg.discordbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discord")
public class DiscordProperties {

    private String token = "";
    private boolean enabled = true;
    private double rareEventChance = 0.01;
    /** Probabilité d'attacher l'image DidiBot à une réponse normale. */
    private double avatarImageChance = 0.12;
    private boolean updateAvatarOnStartup = true;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public double getRareEventChance() {
        return rareEventChance;
    }

    public void setRareEventChance(double rareEventChance) {
        this.rareEventChance = rareEventChance;
    }

    public double getAvatarImageChance() {
        return avatarImageChance;
    }

    public void setAvatarImageChance(double avatarImageChance) {
        this.avatarImageChance = avatarImageChance;
    }

    public boolean isUpdateAvatarOnStartup() {
        return updateAvatarOnStartup;
    }

    public void setUpdateAvatarOnStartup(boolean updateAvatarOnStartup) {
        this.updateAvatarOnStartup = updateAvatarOnStartup;
    }
}
