package fr.dwg.discordbot.repository;

import fr.dwg.discordbot.entity.TriggerResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TriggerResponseRepository extends JpaRepository<TriggerResponse, Long> {

    List<TriggerResponse> findByTriggerId(Long triggerId);
}
