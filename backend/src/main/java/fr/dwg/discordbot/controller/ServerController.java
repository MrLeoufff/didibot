package fr.dwg.discordbot.controller;

import fr.dwg.discordbot.dto.DiscordServerDto;
import fr.dwg.discordbot.dto.DiscordServerRequest;
import fr.dwg.discordbot.service.ServerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/servers")
public class ServerController {

    private final ServerService serverService;

    public ServerController(ServerService serverService) {
        this.serverService = serverService;
    }

    @GetMapping
    public List<DiscordServerDto> findAll() {
        return serverService.findAll();
    }

    @GetMapping("/{id}")
    public DiscordServerDto findById(@PathVariable Long id) {
        return serverService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DiscordServerDto create(@Valid @RequestBody DiscordServerRequest request) {
        return serverService.create(request);
    }

    @PutMapping("/{id}")
    public DiscordServerDto update(@PathVariable Long id, @Valid @RequestBody DiscordServerRequest request) {
        return serverService.update(id, request);
    }

    @PatchMapping("/{id}/enable")
    public DiscordServerDto enable(@PathVariable Long id) {
        return serverService.setEnabled(id, true);
    }

    @PatchMapping("/{id}/disable")
    public DiscordServerDto disable(@PathVariable Long id) {
        return serverService.setEnabled(id, false);
    }
}
