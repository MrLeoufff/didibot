package fr.dwg.discordbot.service;

import fr.dwg.discordbot.dto.IncomingMessage;
import fr.dwg.discordbot.dto.TriggerExecutionDto;
import fr.dwg.discordbot.entity.DiscordServer;
import fr.dwg.discordbot.entity.Trigger;
import fr.dwg.discordbot.entity.TriggerExecution;
import fr.dwg.discordbot.repository.TriggerExecutionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TriggerExecutionService {

    private final TriggerExecutionRepository triggerExecutionRepository;

    public TriggerExecutionService(TriggerExecutionRepository triggerExecutionRepository) {
        this.triggerExecutionRepository = triggerExecutionRepository;
    }

    @Transactional
    public void logExecution(Trigger trigger, IncomingMessage message, String responseContent) {
        TriggerExecution execution = new TriggerExecution();
        execution.setTrigger(trigger);
        DiscordServer server = trigger.getDiscordServer();
        execution.setDiscordServer(server);
        execution.setDiscordGuildId(message.guildId());
        execution.setChannelId(message.channelId());
        execution.setChannelName(message.channelName());
        execution.setUserId(message.userId());
        execution.setUsername(message.username());
        execution.setMatchedPattern(trigger.getPattern());
        execution.setTriggerName(trigger.getName());
        execution.setResponseContent(responseContent);
        triggerExecutionRepository.save(execution);
    }

    @Transactional(readOnly = true)
    public Page<TriggerExecutionDto> findAll(String guildId, Pageable pageable) {
        Page<TriggerExecution> page = (guildId == null || guildId.isBlank())
                ? triggerExecutionRepository.findAllByOrderByExecutedAtDesc(pageable)
                : triggerExecutionRepository.findByDiscordGuildIdOrderByExecutedAtDesc(guildId, pageable);
        return page.map(this::toDto);
    }

    private TriggerExecutionDto toDto(TriggerExecution execution) {
        TriggerExecutionDto dto = new TriggerExecutionDto();
        dto.setId(execution.getId());
        dto.setTriggerId(execution.getTrigger() != null ? execution.getTrigger().getId() : null);
        dto.setDiscordGuildId(execution.getDiscordGuildId());
        dto.setChannelId(execution.getChannelId());
        dto.setChannelName(execution.getChannelName());
        dto.setUserId(execution.getUserId());
        dto.setUsername(execution.getUsername());
        dto.setMatchedPattern(execution.getMatchedPattern());
        dto.setTriggerName(execution.getTriggerName());
        dto.setResponseContent(execution.getResponseContent());
        dto.setExecutedAt(execution.getExecutedAt());
        return dto;
    }
}
