package fr.dwg.discordbot.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.hibernate.annotations.BatchSize;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trigger_rule")
public class Trigger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 512)
    private String pattern;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TriggerType type;

    @Column(nullable = false)
    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TriggerStatus status = TriggerStatus.APPROVED;

    @Column(name = "proposed_by")
    private String proposedBy;

    @Column(name = "proposed_by_discord_id", length = 32)
    private String proposedByDiscordId;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "cooldown_seconds", nullable = false)
    private int cooldownSeconds = 30;

    @Column(name = "fire_chance", nullable = false)
    private double fireChance = 1.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private TriggerAction action = TriggerAction.REPLY;

    @Column(name = "reaction_emoji", length = 64)
    private String reactionEmoji;

    @Enumerated(EnumType.STRING)
    @Column(name = "cooldown_scope", nullable = false, length = 16)
    private CooldownScope cooldownScope = CooldownScope.SERVER;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel_scope", nullable = false, length = 32)
    private ChannelScope channelScope = ChannelScope.ALL;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "discord_server_id", nullable = false)
    private DiscordServer discordServer;

    @BatchSize(size = 50)
    @OneToMany(mappedBy = "trigger", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TriggerResponse> responses = new ArrayList<>();

    @BatchSize(size = 50)
    @OneToMany(mappedBy = "trigger", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TriggerChannel> channels = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public void addResponse(TriggerResponse response) {
        responses.add(response);
        response.setTrigger(this);
    }

    public void clearResponses() {
        responses.forEach(r -> r.setTrigger(null));
        responses.clear();
    }

    public void addChannel(TriggerChannel channel) {
        channels.add(channel);
        channel.setTrigger(this);
    }

    public void clearChannels() {
        channels.forEach(c -> c.setTrigger(null));
        channels.clear();
    }

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

    public DiscordServer getDiscordServer() {
        return discordServer;
    }

    public void setDiscordServer(DiscordServer discordServer) {
        this.discordServer = discordServer;
    }

    public List<TriggerResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<TriggerResponse> responses) {
        this.responses = responses;
    }

    public List<TriggerChannel> getChannels() {
        return channels;
    }

    public void setChannels(List<TriggerChannel> channels) {
        this.channels = channels;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
