package fr.dwg.discordbot.service;

import fr.dwg.discordbot.dto.IncomingMessage;
import fr.dwg.discordbot.dto.ProcessedReply;
import fr.dwg.discordbot.entity.Trigger;
import fr.dwg.discordbot.entity.TriggerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    public MessageProcessingService(
            TriggerService triggerService,
            PatternMatcherService patternMatcherService,
            ChannelFilterService channelFilterService,
            CooldownService cooldownService,
            ResponseService responseService,
            TriggerExecutionService triggerExecutionService,
            ServerService serverService
    ) {
        this.triggerService = triggerService;
        this.patternMatcherService = patternMatcherService;
        this.channelFilterService = channelFilterService;
        this.cooldownService = cooldownService;
        this.responseService = responseService;
        this.triggerExecutionService = triggerExecutionService;
        this.serverService = serverService;
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

            Optional<TriggerResponse> response = responseService.pickRandomResponse(trigger);
            if (response.isEmpty()) {
                log.warn("Aucune réponse active pour le trigger {}", trigger.getName());
                continue;
            }

            String content = response.get().getContent();
            cooldownService.markTriggered(message.guildId(), trigger.getId());
            triggerExecutionService.logExecution(trigger, message, content);

            log.info(
                    "Trigger '{}' activé par {} dans #{} ({})",
                    trigger.getName(),
                    message.username(),
                    message.channelName(),
                    message.guildId()
            );

            return Optional.of(new ProcessedReply(trigger.getId(), trigger.getName(), content));
        }

        return Optional.empty();
    }
}
