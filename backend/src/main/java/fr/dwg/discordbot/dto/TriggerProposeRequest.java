package fr.dwg.discordbot.dto;

import fr.dwg.discordbot.entity.TriggerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TriggerProposeRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String pattern;

    @NotNull
    private TriggerType type = TriggerType.CONTAINS;

    private int cooldownSeconds = 30;

    private String discordGuildId;

    private String proposedBy;

    private List<String> responses = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public TriggerType getType() {
        return type;
    }

    public void setType(TriggerType type) {
        this.type = type;
    }

    public int getCooldownSeconds() {
        return cooldownSeconds;
    }

    public void setCooldownSeconds(int cooldownSeconds) {
        this.cooldownSeconds = cooldownSeconds;
    }

    public String getDiscordGuildId() {
        return discordGuildId;
    }

    public void setDiscordGuildId(String discordGuildId) {
        this.discordGuildId = discordGuildId;
    }

    public String getProposedBy() {
        return proposedBy;
    }

    public void setProposedBy(String proposedBy) {
        this.proposedBy = proposedBy;
    }

    public List<String> getResponses() {
        return responses;
    }

    public void setResponses(List<String> responses) {
        this.responses = responses;
    }
}
