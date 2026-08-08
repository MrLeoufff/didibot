package fr.dwg.discordbot.repository;

import fr.dwg.discordbot.entity.DiscordServer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiscordServerRepository extends JpaRepository<DiscordServer, Long> {

    Optional<DiscordServer> findByDiscordGuildId(String discordGuildId);

    boolean existsByDiscordGuildId(String discordGuildId);
}
