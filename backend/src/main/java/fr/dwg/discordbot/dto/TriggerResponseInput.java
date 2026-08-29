package fr.dwg.discordbot.dto;

import fr.dwg.discordbot.entity.ResponseRarity;
import jakarta.validation.constraints.NotBlank;

public class TriggerResponseInput {

    @NotBlank(message = "Le texte de la réponse est requis")
    private String content;

    private boolean enabled = true;

    private ResponseRarity rarity = ResponseRarity.NORMAL;

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

    public ResponseRarity getRarity() {
        return rarity;
    }

    public void setRarity(ResponseRarity rarity) {
        this.rarity = rarity;
    }
}
