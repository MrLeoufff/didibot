package fr.dwg.discordbot.repository;

import fr.dwg.discordbot.entity.TriggerExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TriggerExecutionRepository extends JpaRepository<TriggerExecution, Long> {

    Page<TriggerExecution> findAllByOrderByExecutedAtDesc(Pageable pageable);

    Page<TriggerExecution> findByDiscordGuildIdOrderByExecutedAtDesc(String discordGuildId, Pageable pageable);
}
