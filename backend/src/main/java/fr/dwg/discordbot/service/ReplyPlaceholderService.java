package fr.dwg.discordbot.service;

import fr.dwg.discordbot.dto.IncomingMessage;
import org.springframework.stereotype.Service;

@Service
public class ReplyPlaceholderService {

    public String interpolate(String content, IncomingMessage message) {
        if (content == null || content.isBlank() || message == null) {
            return content == null ? "" : content;
        }
        String user = nullToEmpty(message.username());
        String mention = message.userId() == null || message.userId().isBlank()
                ? user
                : "<@" + message.userId() + ">";
        return content
                .replace("{mention}", mention)
                .replace("{user}", user)
                .replace("{channel}", nullToEmpty(message.channelName()))
                .replace("{guild}", nullToEmpty(message.guildName()));
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
