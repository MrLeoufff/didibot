package fr.dwg.discordbot.discord;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DiscordSlashTextsTest {

    @Test
    void helpFitsDiscordMessageLimit() {
        assertTrue(DiscordSlashTexts.HELP.length() <= 2000);
    }
}
