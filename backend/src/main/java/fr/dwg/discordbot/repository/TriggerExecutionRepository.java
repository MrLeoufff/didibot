package fr.dwg.discordbot.repository;

import fr.dwg.discordbot.entity.TriggerExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface TriggerExecutionRepository extends JpaRepository<TriggerExecution, Long> {

    Page<TriggerExecution> findAllByOrderByExecutedAtDesc(Pageable pageable);

    Page<TriggerExecution> findByDiscordGuildIdOrderByExecutedAtDesc(String discordGuildId, Pageable pageable);

    @Query(
            value = """
                    SELECT e FROM TriggerExecution e
                    WHERE (:guildId IS NULL OR :guildId = '' OR e.discordGuildId = :guildId)
                      AND (
                            :q IS NULL OR :q = ''
                            OR LOWER(e.username) LIKE LOWER(CONCAT('%', :q, '%'))
                            OR LOWER(e.triggerName) LIKE LOWER(CONCAT('%', :q, '%'))
                            OR LOWER(e.matchedPattern) LIKE LOWER(CONCAT('%', :q, '%'))
                            OR LOWER(COALESCE(e.channelName, '')) LIKE LOWER(CONCAT('%', :q, '%'))
                            OR LOWER(COALESCE(e.responseContent, '')) LIKE LOWER(CONCAT('%', :q, '%'))
                      )
                    """,
            countQuery = """
                    SELECT COUNT(e) FROM TriggerExecution e
                    WHERE (:guildId IS NULL OR :guildId = '' OR e.discordGuildId = :guildId)
                      AND (
                            :q IS NULL OR :q = ''
                            OR LOWER(e.username) LIKE LOWER(CONCAT('%', :q, '%'))
                            OR LOWER(e.triggerName) LIKE LOWER(CONCAT('%', :q, '%'))
                            OR LOWER(e.matchedPattern) LIKE LOWER(CONCAT('%', :q, '%'))
                            OR LOWER(COALESCE(e.channelName, '')) LIKE LOWER(CONCAT('%', :q, '%'))
                            OR LOWER(COALESCE(e.responseContent, '')) LIKE LOWER(CONCAT('%', :q, '%'))
                      )
                    """
    )
    Page<TriggerExecution> search(
            @Param("guildId") String guildId,
            @Param("q") String query,
            Pageable pageable
    );

    @Query("""
            SELECT COUNT(e) FROM TriggerExecution e
            WHERE (:guildId IS NULL OR :guildId = '' OR e.discordGuildId = :guildId)
            """)
    long countAll(@Param("guildId") String guildId);

    @Query("""
            SELECT COUNT(e) FROM TriggerExecution e
            WHERE e.executedAt >= :since
              AND (:guildId IS NULL OR :guildId = '' OR e.discordGuildId = :guildId)
            """)
    long countSince(@Param("since") Instant since, @Param("guildId") String guildId);

    @Query("""
            SELECT e.triggerName AS name, COUNT(e) AS total
            FROM TriggerExecution e
            WHERE e.executedAt >= :since
              AND (:guildId IS NULL OR :guildId = '' OR e.discordGuildId = :guildId)
            GROUP BY e.triggerName
            ORDER BY COUNT(e) DESC
            """)
    List<NameCountView> topTriggers(
            @Param("since") Instant since,
            @Param("guildId") String guildId,
            Pageable pageable
    );

    @Query("""
            SELECT e.username AS name, COUNT(e) AS total
            FROM TriggerExecution e
            WHERE e.executedAt >= :since
              AND (:guildId IS NULL OR :guildId = '' OR e.discordGuildId = :guildId)
            GROUP BY e.username
            ORDER BY COUNT(e) DESC
            """)
    List<NameCountView> topUsers(
            @Param("since") Instant since,
            @Param("guildId") String guildId,
            Pageable pageable
    );

    interface NameCountView {
        String getName();

        long getTotal();
    }
}
