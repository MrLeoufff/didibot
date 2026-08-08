package fr.dwg.discordbot.dto;

import fr.dwg.discordbot.entity.ChannelScope;
import fr.dwg.discordbot.entity.TriggerType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TriggerRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String pattern;

    @NotNull
    private TriggerType type;

    private boolean enabled = true;

    @Min(0)
    private int cooldownSeconds = 30;

    private ChannelScope channelScope = ChannelScope.ALL;

    private Long discordServerId;

    private String discordGuildId;

    private List<String> responses = new ArrayList<>();

    private List<String> channelIds = new ArrayList<>();

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getCooldownSeconds() {
        return cooldownSeconds;
    }

    public void setCooldownSeconds(int cooldownSeconds) {
        this.cooldownSeconds = cooldownSeconds;
    }

    public ChannelScope getChannelScope() {
        return channelScope;
    }

    public void setChannelScope(ChannelScope channelScope) {
        this.channelScope = channelScope;
    }

    public Long getDiscordServerId() {
        return discordServerId;
    }

    public void setDiscordServerId(Long discordServerId) {
        this.discordServerId = discordServerId;
    }

    public String getDiscordGuildId() {
        return discordGuildId;
    }

    public void setDiscordGuildId(String discordGuildId) {
        this.discordGuildId = discordGuildId;
    }

    public List<String> getResponses() {
        return responses;
    }

    public void setResponses(List<String> responses) {
        this.responses = responses;
    }

    public List<String> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<String> channelIds) {
        this.channelIds = channelIds;
    }
}
