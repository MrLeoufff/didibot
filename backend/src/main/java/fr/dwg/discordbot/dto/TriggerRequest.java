package fr.dwg.discordbot.dto;

import fr.dwg.discordbot.entity.ChannelScope;
import fr.dwg.discordbot.entity.CooldownScope;
import fr.dwg.discordbot.entity.TriggerAction;
import fr.dwg.discordbot.entity.TriggerType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TriggerRequest {

    @NotBlank(message = "Le nom est requis")
    private String name;

    @NotBlank(message = "Le déclencheur est requis")
    private String pattern;

    @NotNull(message = "Le type est requis")
    private TriggerType type;

    private boolean enabled = true;

    @NotNull(message = "Le cooldown est requis")
    @Min(value = 0, message = "Le cooldown ne peut pas être négatif")
    private Integer cooldownSeconds = 30;

    @DecimalMin(value = "0.0", message = "La chance doit être entre 0 et 1")
    @DecimalMax(value = "1.0", message = "La chance doit être entre 0 et 1")
    private Double fireChance = 1.0;

    private TriggerAction action = TriggerAction.REPLY;

    private String reactionEmoji;

    private CooldownScope cooldownScope = CooldownScope.SERVER;

    private ChannelScope channelScope = ChannelScope.ALL;

    private Long discordServerId;

    private String discordGuildId;

    @Valid
    @NotEmpty(message = "Au moins une réponse est requise")
    private List<TriggerResponseInput> responses = new ArrayList<>();

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

    public Integer getCooldownSeconds() {
        return cooldownSeconds;
    }

    public void setCooldownSeconds(Integer cooldownSeconds) {
        this.cooldownSeconds = cooldownSeconds;
    }

    public Double getFireChance() {
        return fireChance;
    }

    public void setFireChance(Double fireChance) {
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

    public List<TriggerResponseInput> getResponses() {
        return responses;
    }

    public void setResponses(List<TriggerResponseInput> responses) {
        this.responses = responses;
    }

    public List<String> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<String> channelIds) {
        this.channelIds = channelIds;
    }
}
