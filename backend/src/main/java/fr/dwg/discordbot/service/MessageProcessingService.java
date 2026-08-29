package fr.dwg.discordbot.service;

import fr.dwg.discordbot.config.DiscordProperties;
import fr.dwg.discordbot.dto.IncomingMessage;
import fr.dwg.discordbot.dto.ProcessedReply;
import fr.dwg.discordbot.entity.Trigger;
import fr.dwg.discordbot.entity.TriggerResponse;
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
    private final DiscordProperties discordProperties;

    public MessageProcessingService(
            TriggerService triggerService,
            PatternMatcherService patternMatcherService,
            ChannelFilterService channelFilterService,
            CooldownService cooldownService,
            ResponseService responseService,
            TriggerExecutionService triggerExecutionService,
            ServerService serverService,
            BotImageService botImageService,
            DiscordProperties discordProperties
    ) {
        this.triggerService = triggerService;
        this.patternMatcherService = patternMatcherService;
        this.channelFilterService = channelFilterService;
        this.cooldownService = cooldownService;
        this.responseService = responseService;
        this.triggerExecutionService = triggerExecutionService;
        this.serverService = serverService;
        this.botImageService = botImageService;
        this.discordProperties = discordProperties;
    }

    @Transactional
    public Optional<ProcessedReply> process(IncomingMessage message) {
        if (message.content() == null || message.content().isBlank()) {
            return Optional.empty();
        }

        serverService.syncGuild(message.guildId(), message.guildName());

        List<Trigger> triggers = triggerService.findActiveByGuildId(message.guildId());
        if (triggers.isEmpty()) {
            // Fallback : règles du serveur placeholder (guildId "0") pour démarrer rapidement
            triggers = triggerService.findActiveByGuildId("0");
        }

        // Motifs plus longs d'abord (ex. JavaScript avant Java, GitHub avant Git)
        triggers = triggers.stream()
                .sorted(Comparator
                        .comparingInt((Trigger t) -> t.getPattern() == null ? 0 : t.getPattern().length())
                        .reversed()
                        .thenComparing(Trigger::getId, Comparator.nullsLast(Long::compareTo)))
                .toList();

        for (Trigger trigger : triggers) {
            if (!channelFilterService.isChannelAllowed(trigger, message.channelId())) {
                continue;
            }
            if (!patternMatcherService.matches(trigger, message.content())) {
                continue;
            }
            if (cooldownService.isOnCooldown(message.guildId(), trigger.getId(), trigger.getCooldownSeconds())) {
                log.debug("Cooldown actif pour trigger {} sur guild {}", trigger.getName(), message.guildId());
                continue;
            }

            Optional<ResponseService.PickedResponse> picked = responseService.pickRandomResponse(trigger);
            if (picked.isEmpty()) {
                log.warn("Aucune réponse active pour le trigger {}", trigger.getName());
                continue;
            }

            TriggerResponse response = picked.get().response();
            String content = response.getContent();
            boolean attachImage = shouldAttachImage(picked.get().rareEvent());
            cooldownService.markTriggered(message.guildId(), trigger.getId());
            triggerExecutionService.logExecution(trigger, message, content);

            log.info(
                    "Trigger '{}' activé par {} dans #{} ({}){}",
                    trigger.getName(),
                    message.username(),
                    message.channelName(),
                    message.guildId(),
                    attachImage ? " + image" : ""
            );

            return Optional.of(new ProcessedReply(trigger.getId(), trigger.getName(), content, attachImage));
        }

        return Optional.empty();
    }

    private boolean shouldAttachImage(boolean rareEvent) {
        if (!botImageService.isAvailable()) {
            return false;
        }
        if (rareEvent) {
            return true;
        }
        double chance = Math.max(0.0, Math.min(1.0, discordProperties.getAvatarImageChance()));
        return chance > 0 && ThreadLocalRandom.current().nextDouble() < chance;
    }
}
