package fr.dwg.discordbot.service;

import fr.dwg.discordbot.dto.IncomingMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReplyPlaceholderServiceTest {

    private final ReplyPlaceholderService service = new ReplyPlaceholderService();

    @Test
    void interpolatesKnownTokens() {
        IncomingMessage message = new IncomingMessage(
                "1", "Mon serveur", "2", "general", "42", "René", "hello"
        );
        String result = service.interpolate(
                "Salut {user} dans #{channel} ({guild}) {mention}",
                message
        );
        assertEquals("Salut René dans #general (Mon serveur) <@42>", result);
    }

    @Test
    void leavesUnknownTokensUntouched() {
        IncomingMessage message = new IncomingMessage("1", "g", "2", "c", "3", "u", "x");
        assertEquals("ok {foo}", service.interpolate("ok {foo}", message));
    }
}
