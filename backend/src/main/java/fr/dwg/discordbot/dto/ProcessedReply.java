package fr.dwg.discordbot.dto;

public record ProcessedReply(
        Long triggerId,
        String triggerName,
        String responseContent,
        boolean attachImage,
        boolean sendMessage,
        String reactionEmoji
) {
}
