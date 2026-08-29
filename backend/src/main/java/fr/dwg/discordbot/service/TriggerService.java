package fr.dwg.discordbot.service;

import fr.dwg.discordbot.dto.ResponseCreateRequest;
import fr.dwg.discordbot.dto.TriggerDto;
import fr.dwg.discordbot.dto.TriggerProposeRequest;
import fr.dwg.discordbot.dto.TriggerRequest;
import fr.dwg.discordbot.dto.TriggerResponseDto;
import fr.dwg.discordbot.dto.TriggerResponseInput;
import fr.dwg.discordbot.entity.ChannelScope;
import fr.dwg.discordbot.entity.ResponseRarity;
import fr.dwg.discordbot.entity.DiscordServer;
import fr.dwg.discordbot.entity.Trigger;
import fr.dwg.discordbot.entity.TriggerChannel;
import fr.dwg.discordbot.entity.TriggerResponse;
import fr.dwg.discordbot.entity.TriggerStatus;
import fr.dwg.discordbot.exception.BadRequestException;
import fr.dwg.discordbot.exception.ResourceNotFoundException;
import fr.dwg.discordbot.repository.TriggerRepository;
import fr.dwg.discordbot.repository.TriggerResponseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class TriggerService {

    private final TriggerRepository triggerRepository;
    private final TriggerResponseRepository triggerResponseRepository;
    private final ServerService serverService;
    private final PatternMatcherService patternMatcherService;

    public TriggerService(
            TriggerRepository triggerRepository,
            TriggerResponseRepository triggerResponseRepository,
            ServerService serverService,
            PatternMatcherService patternMatcherService
    ) {
        this.triggerRepository = triggerRepository;
        this.triggerResponseRepository = triggerResponseRepository;
        this.serverService = serverService;
        this.patternMatcherService = patternMatcherService;
    }

    @Transactional(readOnly = true)
    public List<TriggerDto> findAll() {
        return triggerRepository.findAllDetailed().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<TriggerDto> findPending() {
        return triggerRepository.findPendingDetailed().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public TriggerDto findById(Long id) {
        return toDto(getDetailed(id));
    }

    @Transactional(readOnly = true)
    public List<Trigger> findActiveByGuildId(String guildId) {
        return triggerRepository.findActiveByGuildId(guildId);
    }

    @Transactional
    public TriggerDto create(TriggerRequest request) {
        validateAdminRequest(request);
        DiscordServer server = resolveServer(request.getDiscordServerId(), request.getDiscordGuildId());

        Trigger trigger = new Trigger();
        applyRequest(trigger, request, server);
        trigger.setStatus(TriggerStatus.APPROVED);
        trigger.setReviewedAt(Instant.now());
        applyResponseInputs(trigger, request.getResponses());
        applyChannels(trigger, request.getChannelIds(), request.getChannelScope());

        return toDto(triggerRepository.save(trigger));
    }

    @Transactional
    public TriggerDto propose(TriggerProposeRequest request, String proposedByDiscordId) {
        if (!patternMatcherService.isValidPattern(request.getType(), request.getPattern())) {
            throw new BadRequestException("Motif invalide pour le type " + request.getType());
        }
        if (request.getResponses() == null || request.getResponses().stream().noneMatch(r -> r != null && !r.isBlank())) {
            throw new BadRequestException("Au moins une réponse est requise");
        }

        DiscordServer server = resolveServer(null, request.getDiscordGuildId());
        Trigger trigger = new Trigger();
        trigger.setName(request.getName().trim());
        trigger.setPattern(request.getPattern().trim());
        trigger.setType(request.getType());
        trigger.setEnabled(false);
        trigger.setStatus(TriggerStatus.PENDING);
        trigger.setCooldownSeconds(Math.max(0, request.getCooldownSeconds()));
        trigger.setChannelScope(ChannelScope.ALL);
        trigger.setDiscordServer(server);
        trigger.setProposedBy(
                request.getProposedBy() == null || request.getProposedBy().isBlank()
                        ? "Anonyme"
                        : request.getProposedBy().trim()
        );
        trigger.setProposedByDiscordId(proposedByDiscordId);
        applyResponses(trigger, request.getResponses());

        return toDto(triggerRepository.save(trigger));
    }

    @Transactional
    public TriggerDto approve(Long id) {
        Trigger trigger = getDetailed(id);
        if (trigger.getStatus() != TriggerStatus.PENDING) {
            throw new BadRequestException("Seuls les triggers en attente peuvent être approuvés");
        }
        trigger.setStatus(TriggerStatus.APPROVED);
        trigger.setEnabled(true);
        trigger.setReviewedAt(Instant.now());
        return toDto(trigger);
    }

    @Transactional
    public TriggerDto reject(Long id) {
        Trigger trigger = getDetailed(id);
        if (trigger.getStatus() != TriggerStatus.PENDING) {
            throw new BadRequestException("Seuls les triggers en attente peuvent être refusés");
        }
        trigger.setStatus(TriggerStatus.REJECTED);
        trigger.setEnabled(false);
        trigger.setReviewedAt(Instant.now());
        return toDto(trigger);
    }

    @Transactional
    public TriggerDto update(Long id, TriggerRequest request) {
        validateAdminRequest(request);
        Trigger trigger = getDetailed(id);
        DiscordServer server = resolveServer(request.getDiscordServerId(), request.getDiscordGuildId());

        applyRequest(trigger, request, server);
        trigger.clearResponses();
        trigger.clearChannels();
        applyResponseInputs(trigger, request.getResponses());
        applyChannels(trigger, request.getChannelIds(), request.getChannelScope());

        return toDto(trigger);
    }

    @Transactional
    public void delete(Long id) {
        if (!triggerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Trigger introuvable: " + id);
        }
        triggerRepository.deleteById(id);
    }

    @Transactional
    public TriggerDto setEnabled(Long id, boolean enabled) {
        Trigger trigger = getDetailed(id);
        if (enabled && trigger.getStatus() != TriggerStatus.APPROVED) {
            throw new BadRequestException("Un trigger doit être approuvé avant d'être activé");
        }
        trigger.setEnabled(enabled);
        return toDto(trigger);
    }

    @Transactional
    public TriggerResponseDto addResponse(Long triggerId, ResponseCreateRequest request) {
        Trigger trigger = getDetailed(triggerId);
        TriggerResponse response = new TriggerResponse();
        response.setContent(request.getContent());
        response.setEnabled(request.isEnabled());
        trigger.addResponse(response);
        triggerRepository.save(trigger);
        return toResponseDto(response);
    }

    @Transactional
    public void deleteResponse(Long responseId) {
        TriggerResponse response = triggerResponseRepository.findById(responseId)
                .orElseThrow(() -> new ResourceNotFoundException("Réponse introuvable: " + responseId));
        Trigger trigger = response.getTrigger();
        trigger.getResponses().remove(response);
        response.setTrigger(null);
    }

    private void validateAdminRequest(TriggerRequest request) {
        if (!patternMatcherService.isValidPattern(request.getType(), request.getPattern())) {
            throw new BadRequestException("Motif invalide pour le type " + request.getType());
        }
        if (request.getResponses() == null
                || request.getResponses().stream().noneMatch(r -> r != null && r.getContent() != null && !r.getContent().isBlank())) {
            throw new BadRequestException("Au moins une réponse est requise");
        }
        ChannelScope scope = request.getChannelScope() == null ? ChannelScope.ALL : request.getChannelScope();
        if (scope != ChannelScope.ALL
                && (request.getChannelIds() == null || request.getChannelIds().isEmpty())) {
            throw new BadRequestException("Des salons sont requis pour le scope " + scope);
        }
    }

    private DiscordServer resolveServer(Long discordServerId, String discordGuildId) {
        if (discordServerId != null) {
            return serverService.getEntity(discordServerId);
        }
        if (discordGuildId != null && !discordGuildId.isBlank()) {
            return serverService.syncGuild(discordGuildId, null);
        }
        return serverService.getByGuildId("0");
    }

    private void applyRequest(Trigger trigger, TriggerRequest request, DiscordServer server) {
        trigger.setName(request.getName());
        trigger.setPattern(request.getPattern());
        trigger.setType(request.getType());
        trigger.setEnabled(request.isEnabled());
        trigger.setCooldownSeconds(request.getCooldownSeconds() == null ? 30 : Math.max(0, request.getCooldownSeconds()));
        trigger.setChannelScope(request.getChannelScope() == null ? ChannelScope.ALL : request.getChannelScope());
        trigger.setDiscordServer(server);
    }

    private void applyResponses(Trigger trigger, List<String> responses) {
        if (responses == null) {
            return;
        }
        for (String content : responses) {
            if (content == null || content.isBlank()) {
                continue;
            }
            TriggerResponse response = new TriggerResponse();
            response.setContent(content.trim());
            response.setEnabled(true);
            trigger.addResponse(response);
        }
    }

    private void applyResponseInputs(Trigger trigger, List<TriggerResponseInput> responses) {
        if (responses == null) {
            return;
        }
        for (TriggerResponseInput input : responses) {
            if (input == null || input.getContent() == null || input.getContent().isBlank()) {
                continue;
            }
            TriggerResponse response = new TriggerResponse();
            response.setContent(input.getContent().trim());
            response.setEnabled(input.isEnabled());
            response.setRarity(input.getRarity() == null ? ResponseRarity.NORMAL : input.getRarity());
            trigger.addResponse(response);
        }
    }

    private void applyChannels(Trigger trigger, List<String> channelIds, ChannelScope scope) {
        ChannelScope effectiveScope = scope == null ? ChannelScope.ALL : scope;
        if (effectiveScope == ChannelScope.ALL || channelIds == null) {
            return;
        }
        for (String channelId : channelIds) {
            if (channelId == null || channelId.isBlank()) {
                continue;
            }
            TriggerChannel channel = new TriggerChannel();
            channel.setDiscordChannelId(channelId.trim());
            trigger.addChannel(channel);
        }
    }

    private Trigger getDetailed(Long id) {
        return triggerRepository.findDetailedById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trigger introuvable: " + id));
    }

    private TriggerDto toDto(Trigger trigger) {
        TriggerDto dto = new TriggerDto();
        dto.setId(trigger.getId());
        dto.setName(trigger.getName());
        dto.setPattern(trigger.getPattern());
        dto.setType(trigger.getType());
        dto.setEnabled(trigger.isEnabled());
        dto.setStatus(trigger.getStatus());
        dto.setProposedBy(trigger.getProposedBy());
        dto.setProposedByDiscordId(trigger.getProposedByDiscordId());
        dto.setReviewedAt(trigger.getReviewedAt());
        dto.setCooldownSeconds(trigger.getCooldownSeconds());
        dto.setChannelScope(trigger.getChannelScope());
        dto.setDiscordServerId(trigger.getDiscordServer().getId());
        dto.setDiscordGuildId(trigger.getDiscordServer().getDiscordGuildId());
        dto.setDiscordServerName(trigger.getDiscordServer().getName());
        dto.setResponses(trigger.getResponses().stream().map(this::toResponseDto).toList());
        dto.setChannelIds(trigger.getChannels().stream().map(TriggerChannel::getDiscordChannelId).toList());
        dto.setCreatedAt(trigger.getCreatedAt());
        dto.setUpdatedAt(trigger.getUpdatedAt());
        return dto;
    }

    private TriggerResponseDto toResponseDto(TriggerResponse response) {
        return new TriggerResponseDto(
                response.getId(),
                response.getContent(),
                response.isEnabled(),
                response.getRarity(),
                response.getCreatedAt(),
                response.getUpdatedAt()
        );
    }
}
