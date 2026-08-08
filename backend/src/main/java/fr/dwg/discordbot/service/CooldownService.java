package fr.dwg.discordbot.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CooldownService {

    private final Map<String, Instant> lastTriggerAt = new ConcurrentHashMap<>();

    public boolean isOnCooldown(String guildId, Long triggerId, int cooldownSeconds) {
        if (cooldownSeconds <= 0) {
            return false;
        }
        Instant last = lastTriggerAt.get(key(guildId, triggerId));
        if (last == null) {
            return false;
        }
        return Instant.now().isBefore(last.plusSeconds(cooldownSeconds));
    }

    public void markTriggered(String guildId, Long triggerId) {
        lastTriggerAt.put(key(guildId, triggerId), Instant.now());
    }

    private String key(String guildId, Long triggerId) {
        return guildId + ":" + triggerId;
    }
}
