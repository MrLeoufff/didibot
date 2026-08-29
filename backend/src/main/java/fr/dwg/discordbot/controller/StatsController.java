package fr.dwg.discordbot.controller;

import fr.dwg.discordbot.dto.BotStatsDto;
import fr.dwg.discordbot.service.StatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping
    public BotStatsDto snapshot(@RequestParam(required = false) String guildId) {
        return statsService.snapshot(guildId);
    }
}
