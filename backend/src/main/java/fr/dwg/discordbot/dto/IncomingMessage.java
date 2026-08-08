package fr.dwg.discordbot.dto;

public record IncomingMessage(
        String guildId,
        String guildName,
        String channelId,
        String channelName,
        String userId,
        String username,
        String content
) {
}
