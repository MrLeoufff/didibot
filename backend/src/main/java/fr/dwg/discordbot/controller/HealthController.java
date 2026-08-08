package fr.dwg.discordbot.controller;

import fr.dwg.discordbot.discord.DiscordBot;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final DiscordBot discordBot;

    public HealthController(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    @GetMapping
    public Map<String, Object> health() {
        boolean connected = discordBot.getJda() != null && discordBot.getJda().getStatus().name().equals("CONNECTED");
        return Map.of(
                "status", "UP",
                "discordConnected", connected
        );
    }
}
