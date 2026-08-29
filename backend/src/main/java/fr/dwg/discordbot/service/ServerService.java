package fr.dwg.discordbot.service;

import fr.dwg.discordbot.dto.DiscordServerDto;
import fr.dwg.discordbot.dto.DiscordServerRequest;
import fr.dwg.discordbot.entity.DiscordServer;
import fr.dwg.discordbot.exception.BadRequestException;
import fr.dwg.discordbot.exception.ResourceNotFoundException;
import fr.dwg.discordbot.repository.DiscordServerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServerService {

    private final DiscordServerRepository discordServerRepository;

    public ServerService(DiscordServerRepository discordServerRepository) {
        this.discordServerRepository = discordServerRepository;
    }

    @Transactional(readOnly = true)
    public List<DiscordServerDto> findAll() {
        return discordServerRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<DiscordServer> findAllEntities() {
        return discordServerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public DiscordServerDto findById(Long id) {
        return toDto(getEntity(id));
    }

    @Transactional
    public DiscordServerDto create(DiscordServerRequest request) {
        if (discordServerRepository.existsByDiscordGuildId(request.getDiscordGuildId())) {
            throw new BadRequestException("Un serveur avec ce guildId existe déjà");
        }
        DiscordServer server = new DiscordServer();
        server.setDiscordGuildId(request.getDiscordGuildId());
        server.setName(request.getName());
        server.setEnabled(request.isEnabled());
        return toDto(discordServerRepository.save(server));
    }

    @Transactional
    public DiscordServerDto update(Long id, DiscordServerRequest request) {
        DiscordServer server = getEntity(id);
        if (!server.getDiscordGuildId().equals(request.getDiscordGuildId())
                && discordServerRepository.existsByDiscordGuildId(request.getDiscordGuildId())) {
            throw new BadRequestException("Un serveur avec ce guildId existe déjà");
        }
        server.setDiscordGuildId(request.getDiscordGuildId());
        server.setName(request.getName());
        server.setEnabled(request.isEnabled());
        return toDto(server);
    }

    @Transactional
    public DiscordServerDto setEnabled(Long id, boolean enabled) {
        DiscordServer server = getEntity(id);
        server.setEnabled(enabled);
        return toDto(server);
    }

    @Transactional
    public DiscordServer syncGuild(String guildId, String guildName) {
        return discordServerRepository.findByDiscordGuildId(guildId)
                .map(existing -> {
                    if (guildName != null && !guildName.isBlank() && !guildName.equals(existing.getName())) {
                        existing.setName(guildName);
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    DiscordServer created = new DiscordServer();
                    created.setDiscordGuildId(guildId);
                    created.setName(guildName != null && !guildName.isBlank() ? guildName : "Serveur " + guildId);
                    created.setEnabled(true);
                    return discordServerRepository.save(created);
                });
    }

    @Transactional(readOnly = true)
    public DiscordServer getEntity(Long id) {
        return discordServerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serveur introuvable: " + id));
    }

    @Transactional(readOnly = true)
    public DiscordServer getByGuildId(String guildId) {
        return discordServerRepository.findByDiscordGuildId(guildId)
                .orElseThrow(() -> new ResourceNotFoundException("Serveur introuvable pour guildId: " + guildId));
    }

    private DiscordServerDto toDto(DiscordServer server) {
        DiscordServerDto dto = new DiscordServerDto();
        dto.setId(server.getId());
        dto.setDiscordGuildId(server.getDiscordGuildId());
        dto.setName(server.getName());
        dto.setEnabled(server.isEnabled());
        dto.setCreatedAt(server.getCreatedAt());
        dto.setUpdatedAt(server.getUpdatedAt());
        return dto;
    }
}
