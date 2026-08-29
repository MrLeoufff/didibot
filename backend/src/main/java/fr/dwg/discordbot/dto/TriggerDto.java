package fr.dwg.discordbot.dto;

import fr.dwg.discordbot.entity.ChannelScope;
import fr.dwg.discordbot.entity.CooldownScope;
import fr.dwg.discordbot.entity.TriggerAction;
import fr.dwg.discordbot.entity.TriggerStatus;
import fr.dwg.discordbot.entity.TriggerType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TriggerDto {

    private Long id;
    private String name;
    private String pattern;
    private TriggerType type;
    private boolean enabled;
    private TriggerStatus status;
    private String proposedBy;
    private String proposedByDiscordId;
    private Instant reviewedAt;
    private int cooldownSeconds;
    private double fireChance;
    private TriggerAction action;
    private String reactionEmoji;
    private CooldownScope cooldownScope;
    private ChannelScope channelScope;
    private Long discordServerId;
    private String discordGuildId;
    private String discordServerName;
    private List<TriggerResponseDto> responses = new ArrayList<>();
    private List<String> channelIds = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public TriggerStatus getStatus() {
        return status;
    }

    public void setStatus(TriggerStatus status) {
        this.status = status;
    }

    public String getProposedBy() {
        return proposedBy;
    }

    public void setProposedBy(String proposedBy) {
        this.proposedBy = proposedBy;
    }

    public String getProposedByDiscordId() {
        return proposedByDiscordId;
    }

    public void setProposedByDiscordId(String proposedByDiscordId) {
        this.proposedByDiscordId = proposedByDiscordId;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Instant reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public int getCooldownSeconds() {
        return cooldownSeconds;
    }

    public void setCooldownSeconds(int cooldownSeconds) {
        this.cooldownSeconds = cooldownSeconds;
    }

    public double getFireChance() {
        return fireChance;
    }

    public void setFireChance(double fireChance) {
        this.fireChance = fireChance;
    }

    public TriggerAction getAction() {
        return action;
    }

    public void setAction(TriggerAction action) {
        this.action = action;
    }

    public String getReactionEmoji() {
        return reactionEmoji;
    }

    public void setReactionEmoji(String reactionEmoji) {
        this.reactionEmoji = reactionEmoji;
    }

    public CooldownScope getCooldownScope() {
        return cooldownScope;
    }

    public void setCooldownScope(CooldownScope cooldownScope) {
        this.cooldownScope = cooldownScope;
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

    public String getDiscordServerName() {
        return discordServerName;
    }

    public void setDiscordServerName(String discordServerName) {
        this.discordServerName = discordServerName;
    }

    public List<TriggerResponseDto> getResponses() {
        return responses;
    }

    public void setResponses(List<TriggerResponseDto> responses) {
        this.responses = responses;
    }

    public List<String> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<String> channelIds) {
        this.channelIds = channelIds;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
