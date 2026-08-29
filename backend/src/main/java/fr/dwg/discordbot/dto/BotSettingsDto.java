package fr.dwg.discordbot.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.util.ArrayList;
import java.util.List;

public class BotSettingsDto {

    @DecimalMin(value = "0.0", message = "La chance d'avatar doit être entre 0 et 1")
    @DecimalMax(value = "1.0", message = "La chance d'avatar doit être entre 0 et 1")
    private Double avatarImageChance;

    @DecimalMin(value = "0.0", message = "La chance d'événement rare doit être entre 0 et 1")
    @DecimalMax(value = "1.0", message = "La chance d'événement rare doit être entre 0 et 1")
    private Double rareEventChance;

    private String adminChannelId;

    @Valid
    private List<WelcomeSettingsDto> servers = new ArrayList<>();

    public Double getAvatarImageChance() {
        return avatarImageChance;
    }

    public void setAvatarImageChance(Double avatarImageChance) {
        this.avatarImageChance = avatarImageChance;
    }

    public Double getRareEventChance() {
        return rareEventChance;
    }

    public void setRareEventChance(Double rareEventChance) {
        this.rareEventChance = rareEventChance;
    }

    public String getAdminChannelId() {
        return adminChannelId;
    }

    public void setAdminChannelId(String adminChannelId) {
        this.adminChannelId = adminChannelId;
    }

    public List<WelcomeSettingsDto> getServers() {
        return servers;
    }

    public void setServers(List<WelcomeSettingsDto> servers) {
        this.servers = servers;
    }
}
