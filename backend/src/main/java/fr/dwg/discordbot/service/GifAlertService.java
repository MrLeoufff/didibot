package fr.dwg.discordbot.service;

import fr.dwg.discordbot.dto.IncomingMessage;
import fr.dwg.discordbot.dto.ProcessedReply;
import fr.dwg.discordbot.entity.CooldownScope;
import fr.dwg.discordbot.entity.Trigger;
import fr.dwg.discordbot.entity.TriggerResponse;
import fr.dwg.discordbot.repository.TriggerRepository;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Message.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class GifAlertService {

    private static final Logger log = LoggerFactory.getLogger(GifAlertService.class);

    private final TriggerRepository triggerRepository;
    private final TriggerScopeService triggerScopeService;
    private final ChannelFilterService channelFilterService;
    private final CooldownService cooldownService;
    private final ResponseService responseService;
    private final ReplyPlaceholderService replyPlaceholderService;
    private final TriggerExecutionService triggerExecutionService;
    private final BotImageService botImageService;

    public GifAlertService(
            TriggerRepository triggerRepository,
            TriggerScopeService triggerScopeService,
            ChannelFilterService channelFilterService,
            CooldownService cooldownService,
            ResponseService responseService,
            ReplyPlaceholderService replyPlaceholderService,
            TriggerExecutionService triggerExecutionService,
            BotImageService botImageService
    ) {
        this.triggerRepository = triggerRepository;
        this.triggerScopeService = triggerScopeService;
        this.channelFilterService = channelFilterService;
        this.cooldownService = cooldownService;
        this.responseService = responseService;
        this.replyPlaceholderService = replyPlaceholderService;
        this.triggerExecutionService = triggerExecutionService;
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

    public Optional<ProcessedReply> maybeReply(IncomingMessage incoming, Message message) {
        if (!containsGif(message)) {
            return Optional.empty();
        }
        Optional<Trigger> matched = resolveGifTrigger(incoming.guildId());
        if (matched.isEmpty()) {
            return Optional.empty();
        }
        Trigger trigger = matched.get();
        if (!channelFilterService.isChannelAllowed(trigger, incoming.channelId())) {
            return Optional.empty();
        }
        CooldownScope scope = trigger.getCooldownScope() == null ? CooldownScope.SERVER : trigger.getCooldownScope();
        if (cooldownService.isOnCooldown(
                incoming.guildId(),
                trigger.getId(),
                incoming.userId(),
                trigger.getCooldownSeconds(),
                scope
        )) {
            return Optional.empty();
        }
        if (MessageProcessingService.missesFireChance(trigger)) {
            return Optional.empty();
        }

        Optional<ResponseService.PickedResponse> picked = responseService.pickRandomResponse(trigger);
        if (picked.isEmpty()) {
            log.warn("Alerte GIF sans réponse active");
            return Optional.empty();
        }

        TriggerResponse response = picked.get().response();
        String content = replyPlaceholderService.interpolate(response.getContent(), incoming);
        boolean attachImage = picked.get().rareEvent() && botImageService.isAvailable();
        cooldownService.markTriggered(incoming.guildId(), trigger.getId(), incoming.userId(), scope);
        triggerExecutionService.logExecution(trigger, incoming, content);
        return Optional.of(MessageProcessingService.toProcessed(trigger, content, attachImage));
    }

    private Optional<Trigger> resolveGifTrigger(String guildId) {
        List<Trigger> local = triggerRepository.findActiveGifByGuildId(guildId);
        if (TriggerScopeService.GLOBAL_GUILD_ID.equals(guildId)) {
            return local.stream().findFirst();
        }
        List<Trigger> global = triggerRepository.findActiveGifByGuildId(TriggerScopeService.GLOBAL_GUILD_ID);
        return triggerScopeService.mergeLocalAndGlobal(local, global).stream().findFirst();
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
