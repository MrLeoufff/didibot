package fr.dwg.discordbot.dto;

import java.time.Instant;

public class TriggerResponseDto {

    private Long id;
    private String content;
    private boolean enabled;
    private Instant createdAt;
    private Instant updatedAt;

    public TriggerResponseDto() {
    }

    public TriggerResponseDto(Long id, String content, boolean enabled, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.content = content;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
