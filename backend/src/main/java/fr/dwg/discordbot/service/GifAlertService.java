package fr.dwg.discordbot.service;

import fr.dwg.discordbot.dto.ProcessedReply;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Message.Attachment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class GifAlertService {

    private static final long GIF_COOLDOWN_ID = -9001L;
    private static final int COOLDOWN_SECONDS = 45;

    private static final List<String> RESPONSES = List.of(
            "🚨 Alerte boomer : un GIF vient d'atterrir.",
            "Boomer alert. Quelqu'un a sorti le GIF.",
            "GIF détecté. Les anciens sont en danger.",
            "Tenor a encore frappé. Alerte au boomer.",
            "Un GIF ? En 2026 ? Courage.",
            "Alerte millennial/boomer : format GIF activé.",
            "Ce GIF a probablement 12 ans. Comme le meme.",
            "DidiBot (team Java) valide le GIF... à contrecœur.",
            "🚨 GIF incoming. Rangez vos PowerPoint."
    );

    private final CooldownService cooldownService;
    private final BotImageService botImageService;

    public GifAlertService(CooldownService cooldownService, BotImageService botImageService) {
        this.cooldownService = cooldownService;
        this.botImageService = botImageService;
    }

    public boolean containsGif(Message message) {
        for (Attachment attachment : message.getAttachments()) {
            String contentType = attachment.getContentType();
            String fileName = attachment.getFileName();
            if (contentType != null && contentType.toLowerCase(Locale.ROOT).contains("gif")) {
                return true;
            }
            if (fileName != null && fileName.toLowerCase(Locale.ROOT).endsWith(".gif")) {
                return true;
            }
        }

        for (MessageEmbed embed : message.getEmbeds()) {
            if (embed.getType() == EmbedType.GIFV) {
                return true;
            }
            if (looksLikeGifUrl(embed.getUrl())
                    || (embed.getImage() != null && looksLikeGifUrl(embed.getImage().getUrl()))
                    || (embed.getThumbnail() != null && looksLikeGifUrl(embed.getThumbnail().getUrl()))) {
                return true;
            }
            if (embed.getDescription() != null && looksLikeGifUrl(embed.getDescription())) {
                return true;
            }
        }

        String content = message.getContentRaw();
        if (content == null || content.isBlank()) {
            return false;
        }
        String lower = content.toLowerCase(Locale.ROOT);
        return lower.contains("tenor.com")
                || lower.contains("giphy.com")
                || lower.contains("media.tenor")
                || lower.contains(".gif");
    }

    public Optional<ProcessedReply> maybeReply(String guildId, Message message) {
        if (!containsGif(message)) {
            return Optional.empty();
        }
        if (cooldownService.isOnCooldown(guildId, GIF_COOLDOWN_ID, COOLDOWN_SECONDS)) {
            return Optional.empty();
        }

        cooldownService.markTriggered(guildId, GIF_COOLDOWN_ID);
        String content = RESPONSES.get(ThreadLocalRandom.current().nextInt(RESPONSES.size()));
        boolean attachImage = botImageService.isAvailable()
                && ThreadLocalRandom.current().nextDouble() < 0.25;
        return Optional.of(new ProcessedReply(GIF_COOLDOWN_ID, "🚨 Alerte GIF", content, attachImage));
    }

    private boolean looksLikeGifUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        String lower = url.toLowerCase(Locale.ROOT);
        return lower.contains("tenor.com")
                || lower.contains("giphy.com")
                || lower.contains("media.tenor")
                || lower.endsWith(".gif")
                || lower.contains(".gif?");
    }
}
