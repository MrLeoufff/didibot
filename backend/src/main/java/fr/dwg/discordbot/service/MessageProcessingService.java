package fr.dwg.discordbot.service;

import fr.dwg.discordbot.dto.IncomingMessage;
import fr.dwg.discordbot.dto.ProcessedReply;
import fr.dwg.discordbot.entity.CooldownScope;
import fr.dwg.discordbot.entity.Trigger;
import fr.dwg.discordbot.entity.TriggerAction;
import fr.dwg.discordbot.entity.TriggerResponse;
import fr.dwg.discordbot.entity.TriggerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MessageProcessingService {

    private static final Logger log = LoggerFactory.getLogger(MessageProcessingService.class);

    private final TriggerService triggerService;
    private final PatternMatcherService patternMatcherService;
    private final ChannelFilterService channelFilterService;
    private final CooldownService cooldownService;
    private final ResponseService responseService;
    private final TriggerExecutionService triggerExecutionService;
    private final ServerService serverService;
    private final BotImageService botImageService;
    private final BotSettingsService botSettingsService;
    private final TriggerScopeService triggerScopeService;
    private final ReplyPlaceholderService replyPlaceholderService;

    public MessageProcessingService(
            TriggerService triggerService,
            PatternMatcherService patternMatcherService,
            ChannelFilterService channelFilterService,
            CooldownService cooldownService,
            ResponseService responseService,
            TriggerExecutionService triggerExecutionService,
            ServerService serverService,
            BotImageService botImageService,
            BotSettingsService botSettingsService,
            TriggerScopeService triggerScopeService,
            ReplyPlaceholderService replyPlaceholderService
    ) {
        this.triggerService = triggerService;
        this.patternMatcherService = patternMatcherService;
        this.channelFilterService = channelFilterService;
        this.cooldownService = cooldownService;
        this.responseService = responseService;
        this.triggerExecutionService = triggerExecutionService;
        this.serverService = serverService;
        this.botImageService = botImageService;
        this.botSettingsService = botSettingsService;
        this.triggerScopeService = triggerScopeService;
        this.replyPlaceholderService = replyPlaceholderService;
    }

    private List<Trigger> resolveActiveTriggers(String guildId) {
        List<Trigger> local = triggerService.findActiveByGuildId(guildId);
        if (TriggerScopeService.GLOBAL_GUILD_ID.equals(guildId)) {
            return local;
        }
        List<Trigger> global = triggerService.findActiveByGuildId(TriggerScopeService.GLOBAL_GUILD_ID);
        return triggerScopeService.mergeLocalAndGlobal(local, global);
    }

    @Transactional
    public Optional<ProcessedReply> process(IncomingMessage message) {
        if (message.content() == null || message.content().isBlank()) {
            return Optional.empty();
        }

        serverService.syncGuild(message.guildId(), message.guildName());

        List<Trigger> triggers = resolveActiveTriggers(message.guildId());

        // Motifs plus longs d'abord (ex. JavaScript avant Java, GitHub avant Git)
        triggers = triggers.stream()
                .sorted(Comparator
                        .comparingInt((Trigger t) -> t.getPattern() == null ? 0 : t.getPattern().length())
                        .reversed()
                        .thenComparing(Trigger::getId, Comparator.nullsLast(Long::compareTo)))
                .toList();

        for (Trigger trigger : triggers) {
            if (trigger.getType() == TriggerType.GIF) {
                continue;
            }
            if (!channelFilterService.isChannelAllowed(trigger, message.channelId())) {
                continue;
            }
            if (!patternMatcherService.matches(trigger, message.content())) {
                continue;
            }
            CooldownScope scope = trigger.getCooldownScope() == null ? CooldownScope.SERVER : trigger.getCooldownScope();
            if (cooldownService.isOnCooldown(
                    message.guildId(),
                    trigger.getId(),
                    message.userId(),
                    trigger.getCooldownSeconds(),
                    scope
            )) {
                log.debug("Cooldown actif pour trigger {} sur guild {}", trigger.getName(), message.guildId());
                continue;
            }
            if (missesFireChance(trigger)) {
                log.debug("Chance ratée pour trigger {} ({})", trigger.getName(), trigger.getFireChance());
                continue;
            }

            Optional<ResponseService.PickedResponse> picked = responseService.pickRandomResponse(trigger);
            if (picked.isEmpty()) {
                log.warn("Aucune réponse active pour le trigger {}", trigger.getName());
                continue;
            }

            TriggerResponse response = picked.get().response();
            String content = replyPlaceholderService.interpolate(response.getContent(), message);
            boolean attachImage = shouldAttachImage(picked.get().rareEvent());
            cooldownService.markTriggered(message.guildId(), trigger.getId(), message.userId(), scope);
            triggerExecutionService.logExecution(trigger, message, content);

            log.info(
                    "Trigger '{}' activé par {} dans #{} ({}){}",
                    trigger.getName(),
                    message.username(),
                    message.channelName(),
                    message.guildId(),
                    attachImage ? " + image" : ""
            );

            return Optional.of(toProcessed(trigger, content, attachImage));
        }

        return Optional.empty();
    }

    static ProcessedReply toProcessed(Trigger trigger, String content, boolean attachImage) {
        TriggerAction action = trigger.getAction() == null ? TriggerAction.REPLY : trigger.getAction();
        boolean sendMessage = action == TriggerAction.REPLY || action == TriggerAction.BOTH;
        String emoji = (action == TriggerAction.REACT || action == TriggerAction.BOTH)
                ? trigger.getReactionEmoji()
                : null;
        if (sendMessage && (content == null || content.isBlank()) && emoji != null && !emoji.isBlank()) {
            sendMessage = false;
        }
        return new ProcessedReply(
                trigger.getId(),
                trigger.getName(),
                content,
                attachImage && sendMessage,
                sendMessage && content != null && !content.isBlank(),
                emoji
        );
    }

    static boolean missesFireChance(Trigger trigger) {
        double chance = trigger.getFireChance();
        if (chance >= 1.0) {
            return false;
        }
        if (chance <= 0.0) {
            return true;
        }
        return ThreadLocalRandom.current().nextDouble() >= chance;
    }

    private boolean shouldAttachImage(boolean rareEvent) {
        if (!botImageService.isAvailable()) {
            return false;
        }
        if (rareEvent) {
            return true;
        }
        double chance = Math.max(0.0, Math.min(1.0, botSettingsService.getAvatarImageChance()));
        return chance > 0 && ThreadLocalRandom.current().nextDouble() < chance;
    }
}
