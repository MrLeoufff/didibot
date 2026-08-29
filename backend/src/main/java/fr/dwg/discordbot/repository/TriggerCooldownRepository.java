package fr.dwg.discordbot.repository;

import fr.dwg.discordbot.entity.TriggerCooldown;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TriggerCooldownRepository extends JpaRepository<TriggerCooldown, Long> {

    Optional<TriggerCooldown> findByTriggerIdAndGuildIdAndUserKey(Long triggerId, String guildId, String userKey);
}
