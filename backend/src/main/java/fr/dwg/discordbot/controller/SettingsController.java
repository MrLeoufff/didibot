package fr.dwg.discordbot.controller;

import fr.dwg.discordbot.dto.BotSettingsDto;
import fr.dwg.discordbot.service.BotSettingsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final BotSettingsService botSettingsService;

    public SettingsController(BotSettingsService botSettingsService) {
        this.botSettingsService = botSettingsService;
    }

    @GetMapping
    public BotSettingsDto get() {
        return botSettingsService.get();
    }

    @PutMapping
    public BotSettingsDto save(@Valid @RequestBody BotSettingsDto request) {
        return botSettingsService.save(request);
    }
}
