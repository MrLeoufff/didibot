package fr.dwg.discordbot.repository;

import fr.dwg.discordbot.entity.ResponseRarity;
import fr.dwg.discordbot.entity.TriggerResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TriggerResponseRepository extends JpaRepository<TriggerResponse, Long> {

    List<TriggerResponse> findByTriggerId(Long triggerId);

    @Query("""
            SELECT r FROM TriggerResponse r
            JOIN FETCH r.trigger t
            WHERE r.enabled = true
              AND r.rarity = :rarity
              AND t.name = :triggerName
            """)
    List<TriggerResponse> findEnabledByTriggerNameAndRarity(String triggerName, ResponseRarity rarity);
}
