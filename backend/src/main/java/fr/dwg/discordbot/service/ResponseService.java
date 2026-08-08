package fr.dwg.discordbot.service;

import fr.dwg.discordbot.entity.Trigger;
import fr.dwg.discordbot.entity.TriggerResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ResponseService {

    public Optional<TriggerResponse> pickRandomResponse(Trigger trigger) {
        List<TriggerResponse> enabled = trigger.getResponses().stream()
                .filter(TriggerResponse::isEnabled)
                .toList();

        if (enabled.isEmpty()) {
            return Optional.empty();
        }

        int index = ThreadLocalRandom.current().nextInt(enabled.size());
        return Optional.of(enabled.get(index));
    }
}
