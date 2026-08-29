package fr.dwg.discordbot.repository;

import fr.dwg.discordbot.entity.Trigger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TriggerRepository extends JpaRepository<Trigger, Long> {

    @Query("""
            SELECT DISTINCT t FROM Trigger t
            LEFT JOIN FETCH t.discordServer
            LEFT JOIN FETCH t.responses
            ORDER BY t.id
            """)
    List<Trigger> findAllDetailed();

    @Query("""
            SELECT DISTINCT t FROM Trigger t
            LEFT JOIN FETCH t.discordServer
            LEFT JOIN FETCH t.responses
            WHERE t.id = :id
            """)
    Optional<Trigger> findDetailedById(@Param("id") Long id);

    @Query("""
            SELECT DISTINCT t FROM Trigger t
            LEFT JOIN FETCH t.discordServer
            LEFT JOIN FETCH t.responses
            WHERE t.enabled = true
              AND t.status = fr.dwg.discordbot.entity.TriggerStatus.APPROVED
              AND t.discordServer.enabled = true
              AND t.discordServer.discordGuildId = :guildId
            """)
    List<Trigger> findActiveByGuildId(@Param("guildId") String guildId);

    @Query("""
            SELECT DISTINCT t FROM Trigger t
            LEFT JOIN FETCH t.discordServer
            LEFT JOIN FETCH t.responses
            WHERE t.status = fr.dwg.discordbot.entity.TriggerStatus.PENDING
            ORDER BY t.createdAt DESC
            """)
    List<Trigger> findPendingDetailed();

    @Query("""
            SELECT DISTINCT t FROM Trigger t
            LEFT JOIN FETCH t.discordServer
            LEFT JOIN FETCH t.responses
            WHERE t.discordServer.id = :discordServerId
            """)
    List<Trigger> findByDiscordServerId(@Param("discordServerId") Long discordServerId);
}

