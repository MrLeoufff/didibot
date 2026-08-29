package fr.dwg.discordbot.service;

import fr.dwg.discordbot.entity.ResponseRarity;
import fr.dwg.discordbot.entity.Trigger;
import fr.dwg.discordbot.entity.TriggerResponse;
import fr.dwg.discordbot.repository.TriggerResponseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ResponseService {

    public static final String RARE_EVENTS_TRIGGER_NAME = "✨ Événements rares";

    private static final Logger log = LoggerFactory.getLogger(ResponseService.class);

    private final TriggerResponseRepository triggerResponseRepository;
    private final BotSettingsService botSettingsService;

    public ResponseService(
            TriggerResponseRepository triggerResponseRepository,
            BotSettingsService botSettingsService
    ) {
        this.triggerResponseRepository = triggerResponseRepository;
        this.botSettingsService = botSettingsService;
    }

    public Optional<PickedResponse> pickRandomResponse(Trigger trigger) {
        List<TriggerResponse> enabled = trigger.getResponses().stream()
                .filter(TriggerResponse::isEnabled)
                .toList();

        if (enabled.isEmpty()) {
            return Optional.empty();
        }

        List<TriggerResponse> normal = enabled.stream()
                .filter(r -> r.getRarity() == null || r.getRarity() == ResponseRarity.NORMAL)
                .toList();
        List<TriggerResponse> localRare = enabled.stream()
                .filter(r -> r.getRarity() == ResponseRarity.RARE)
                .toList();

        double rareEventChance = botSettingsService.getRareEventChance();
        if (rareEventChance > 0 && ThreadLocalRandom.current().nextDouble() < rareEventChance) {
            List<TriggerResponse> rarePool = new ArrayList<>(localRare);
            rarePool.addAll(triggerResponseRepository.findEnabledByTriggerNameAndRarity(
                    RARE_EVENTS_TRIGGER_NAME,
                    ResponseRarity.RARE
            ));
            if (!rarePool.isEmpty()) {
                TriggerResponse rare = pick(rarePool);
                log.info("Événement rare déclenché pour '{}'", trigger.getName());
                return Optional.of(new PickedResponse(rare, true));
            }
        }

        List<TriggerResponse> pool = normal.isEmpty() ? enabled : normal;
        TriggerResponse chosen = pick(pool);
        boolean rare = chosen.getRarity() == ResponseRarity.RARE;
        return Optional.of(new PickedResponse(chosen, rare));
    }

    private TriggerResponse pick(List<TriggerResponse> responses) {
        int index = ThreadLocalRandom.current().nextInt(responses.size());
        return responses.get(index);
    }

    public record PickedResponse(TriggerResponse response, boolean rareEvent) {
    }
}
