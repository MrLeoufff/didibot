package fr.dwg.discordbot.service;

import fr.dwg.discordbot.entity.ChannelScope;
import fr.dwg.discordbot.entity.Trigger;
import fr.dwg.discordbot.entity.TriggerChannel;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChannelFilterService {

    public boolean isChannelAllowed(Trigger trigger, String channelId) {
        ChannelScope scope = trigger.getChannelScope();
        if (scope == null || scope == ChannelScope.ALL) {
            return true;
        }

        Set<String> configured = trigger.getChannels().stream()
                .map(TriggerChannel::getDiscordChannelId)
                .collect(Collectors.toSet());

        return switch (scope) {
            case INCLUDE -> configured.contains(channelId);
            case EXCLUDE -> !configured.contains(channelId);
            case ALL -> true;
        };
    }
}
