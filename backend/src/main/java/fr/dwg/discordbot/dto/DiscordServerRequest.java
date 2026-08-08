package fr.dwg.discordbot.dto;

import jakarta.validation.constraints.NotBlank;

public class DiscordServerRequest {

    @NotBlank
    private String discordGuildId;

    @NotBlank
    private String name;

    private boolean enabled = true;

    public String getDiscordGuildId() {
        return discordGuildId;
    }

    public void setDiscordGuildId(String discordGuildId) {
        this.discordGuildId = discordGuildId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
