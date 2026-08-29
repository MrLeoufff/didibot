package fr.dwg.discordbot.service;

import fr.dwg.discordbot.entity.CooldownScope;
import fr.dwg.discordbot.entity.TriggerCooldown;
import fr.dwg.discordbot.repository.TriggerCooldownRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CooldownService {

    private final Map<String, Instant> lastTriggerAt = new ConcurrentHashMap<>();
    private final TriggerCooldownRepository triggerCooldownRepository;

    public CooldownService(TriggerCooldownRepository triggerCooldownRepository) {
        this.triggerCooldownRepository = triggerCooldownRepository;
    }

    public boolean isOnCooldown(String guildId, Long triggerId, int cooldownSeconds) {
        return isOnCooldown(guildId, triggerId, null, cooldownSeconds, CooldownScope.SERVER);
    }

    public boolean isOnCooldown(
            String guildId,
            Long triggerId,
            String userId,
            int cooldownSeconds,
            CooldownScope scope
    ) {
        if (cooldownSeconds <= 0 || triggerId == null) {
            return false;
        }
        String key = key(guildId, triggerId, userId, scope);
        Instant last = lastTriggerAt.get(key);
        if (last == null && triggerCooldownRepository != null) {
            last = triggerCooldownRepository
                    .findByTriggerIdAndGuildIdAndUserKey(triggerId, guildId, userKey(userId, scope))
                    .map(TriggerCooldown::getLastFiredAt)
                    .orElse(null);
            if (last != null) {
                lastTriggerAt.put(key, last);
            }
        }
        if (last == null) {
            return false;
        }
        return Instant.now().isBefore(last.plusSeconds(cooldownSeconds));
    }

    public void markTriggered(String guildId, Long triggerId) {
        markTriggered(guildId, triggerId, null, CooldownScope.SERVER);
    }

    @Transactional
    public void markTriggered(String guildId, Long triggerId, String userId, CooldownScope scope) {
        if (triggerId == null || guildId == null) {
            return;
        }
        Instant now = Instant.now();
        String scopeUser = userKey(userId, scope);
        lastTriggerAt.put(key(guildId, triggerId, userId, scope), now);
        if (triggerCooldownRepository == null) {
            return;
        }
        TriggerCooldown row = triggerCooldownRepository
                .findByTriggerIdAndGuildIdAndUserKey(triggerId, guildId, scopeUser)
                .orElseGet(TriggerCooldown::new);
        row.setTriggerId(triggerId);
        row.setGuildId(guildId);
        row.setUserKey(scopeUser);
        row.setLastFiredAt(now);
        triggerCooldownRepository.save(row);
    }

    private String key(String guildId, Long triggerId, String userId, CooldownScope scope) {
        return guildId + ":" + triggerId + ":" + userKey(userId, scope);
    }

    private static String userKey(String userId, CooldownScope scope) {
        if (scope != CooldownScope.USER) {
            return "";
        }
        return userId == null ? "" : userId;
    }
}
