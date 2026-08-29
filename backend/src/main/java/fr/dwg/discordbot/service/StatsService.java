package fr.dwg.discordbot.service;

import fr.dwg.discordbot.dto.BotStatsDto;
import fr.dwg.discordbot.dto.NameCountDto;
import fr.dwg.discordbot.entity.Trigger;
import fr.dwg.discordbot.entity.TriggerStatus;
import fr.dwg.discordbot.repository.DiscordServerRepository;
import fr.dwg.discordbot.repository.TriggerExecutionRepository;
import fr.dwg.discordbot.repository.TriggerRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class StatsService {

    static final ZoneId STATS_ZONE = ZoneId.of("Europe/Paris");
    private static final int TOP_LIMIT = 5;

    private final TriggerExecutionRepository triggerExecutionRepository;
    private final TriggerRepository triggerRepository;
    private final DiscordServerRepository discordServerRepository;
    private final TriggerService triggerService;
    private final TriggerScopeService triggerScopeService;

    public StatsService(
            TriggerExecutionRepository triggerExecutionRepository,
            TriggerRepository triggerRepository,
            DiscordServerRepository discordServerRepository,
            TriggerService triggerService,
            TriggerScopeService triggerScopeService
    ) {
        this.triggerExecutionRepository = triggerExecutionRepository;
        this.triggerRepository = triggerRepository;
        this.discordServerRepository = discordServerRepository;
        this.triggerService = triggerService;
        this.triggerScopeService = triggerScopeService;
    }

    @Transactional(readOnly = true)
    public BotStatsDto snapshot(String guildId) {
        String guild = guildId == null || guildId.isBlank() ? "" : guildId.trim();
        Instant startOfToday = ZonedDateTime.now(STATS_ZONE).toLocalDate().atStartOfDay(STATS_ZONE).toInstant();
        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        var topPage = PageRequest.of(0, TOP_LIMIT);

        BotStatsDto dto = new BotStatsDto();
        dto.setRepliesToday(triggerExecutionRepository.countSince(startOfToday, guild));
        dto.setRepliesLast7Days(triggerExecutionRepository.countSince(sevenDaysAgo, guild));
        dto.setRepliesAllTime(triggerExecutionRepository.countAll(guild));
        dto.setActiveTriggers(countActiveTriggers(guild.isEmpty() ? null : guild));
        dto.setPendingTriggers(triggerRepository.countByStatus(TriggerStatus.PENDING));
        dto.setServerCount(discordServerRepository.count());
        dto.setTopTriggers(toDto(triggerExecutionRepository.topTriggers(sevenDaysAgo, guild, topPage)));
        dto.setTopUsers(toDto(triggerExecutionRepository.topUsers(sevenDaysAgo, guild, topPage)));
        return dto;
    }

    private long countActiveTriggers(String guildId) {
        if (guildId == null) {
            return triggerRepository.countActiveApproved();
        }
        List<Trigger> local = triggerService.findActiveByGuildId(guildId);
        List<Trigger> global = triggerService.findActiveByGuildId(TriggerScopeService.GLOBAL_GUILD_ID);
        return triggerScopeService.mergeLocalAndGlobal(local, global).stream()
                .filter(trigger -> trigger.getPattern() == null || !trigger.getPattern().startsWith("___"))
                .count();
    }

    private static List<NameCountDto> toDto(List<TriggerExecutionRepository.NameCountView> rows) {
        return rows.stream()
                .map(row -> new NameCountDto(row.getName() == null ? "?" : row.getName(), row.getTotal()))
                .toList();
    }

}
