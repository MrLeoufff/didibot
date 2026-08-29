package fr.dwg.discordbot.service;

import fr.dwg.discordbot.entity.CooldownScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CooldownServiceTest {

    private CooldownService cooldownService;

    @BeforeEach
    void setUp() {
        cooldownService = new CooldownService(null);
    }

    @Test
    void marksCooldownPerGuildAndTrigger() {
        assertFalse(cooldownService.isOnCooldown("guild-1", 1L, 30));
        cooldownService.markTriggered("guild-1", 1L);
        assertTrue(cooldownService.isOnCooldown("guild-1", 1L, 30));
        assertFalse(cooldownService.isOnCooldown("guild-2", 1L, 30));
        assertFalse(cooldownService.isOnCooldown("guild-1", 2L, 30));
    }

    @Test
    void zeroCooldownAlwaysAllows() {
        cooldownService.markTriggered("guild-1", 1L);
        assertFalse(cooldownService.isOnCooldown("guild-1", 1L, 0));
    }

    @Test
    void userScopeDoesNotMuteTheWholeGuild() {
        cooldownService.markTriggered("guild-1", 1L, "alice", CooldownScope.USER);
        assertTrue(cooldownService.isOnCooldown("guild-1", 1L, "alice", 30, CooldownScope.USER));
        assertFalse(cooldownService.isOnCooldown("guild-1", 1L, "bob", 30, CooldownScope.USER));
    }
}
