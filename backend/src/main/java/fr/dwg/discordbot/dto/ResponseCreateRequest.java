package fr.dwg.discordbot.dto;

import jakarta.validation.constraints.NotBlank;

public class ResponseCreateRequest {

    @NotBlank
    private String content;

    private boolean enabled = true;

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
}
