package fr.dwg.discordbot.discord;

import fr.dwg.discordbot.dto.BotStatsDto;
import fr.dwg.discordbot.dto.NameCountDto;
import fr.dwg.discordbot.dto.TriggerProposeRequest;
import fr.dwg.discordbot.entity.Trigger;
import fr.dwg.discordbot.entity.TriggerType;
import fr.dwg.discordbot.service.StatsService;
import fr.dwg.discordbot.service.TriggerScopeService;
import fr.dwg.discordbot.service.TriggerService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class DiscordSlashCommandListener extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(DiscordSlashCommandListener.class);
    private static final int DISCORD_MAX = 1900;
    private static final int TRIGGERS_SHOWN = 20;

    private final TriggerService triggerService;
    private final TriggerScopeService triggerScopeService;
    private final StatsService statsService;

    public DiscordSlashCommandListener(
            TriggerService triggerService,
            TriggerScopeService triggerScopeService,
            StatsService statsService
    ) {
        this.triggerService = triggerService;
        this.triggerScopeService = triggerScopeService;
        this.statsService = statsService;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "ping" -> handlePing(event);
            case "help" -> handleHelp(event);
            case "triggers" -> handleTriggers(event);
            case "stats" -> handleStats(event);
            case "propose-trigger" -> handlePropose(event);
            default -> { }
        }
    }

    private void handlePing(SlashCommandInteractionEvent event) {
        event.reply("Pong — " + event.getJDA().getGatewayPing() + " ms").queue();
    }

    private void handleHelp(SlashCommandInteractionEvent event) {
        event.reply(DiscordSlashTexts.HELP).queue();
    }

    private void handleTriggers(SlashCommandInteractionEvent event) {
        if (!requireGuild(event)) {
            return;
        }
        event.deferReply(true).queue();
        try {
            String guildId = event.getGuild().getId();
            List<Trigger> local = triggerService.findActiveByGuildId(guildId);
            List<Trigger> global = triggerService.findActiveByGuildId(TriggerScopeService.GLOBAL_GUILD_ID);
            List<Trigger> merged = triggerScopeService.mergeLocalAndGlobal(local, global).stream()
                    .filter(trigger -> trigger.getPattern() == null || !trigger.getPattern().startsWith("___"))
                    .sorted(Comparator.comparing(Trigger::getName, String.CASE_INSENSITIVE_ORDER))
                    .toList();

            if (merged.isEmpty()) {
                event.getHook().sendMessage("Aucun trigger actif sur ce serveur.").queue();
                return;
            }

            StringBuilder body = new StringBuilder();
            body.append("**Triggers actifs (").append(merged.size()).append(")**\n");
            int shown = Math.min(TRIGGERS_SHOWN, merged.size());
            for (int i = 0; i < shown; i++) {
                Trigger trigger = merged.get(i);
                String line = "• **" + trigger.getName() + "** — `" + trigger.getPattern() + "` ("
                        + trigger.getType() + ")\n";
                if (body.length() + line.length() > DISCORD_MAX) {
                    break;
                }
                body.append(line);
            }
            if (merged.size() > shown) {
                body.append("_… et ").append(merged.size() - shown).append(" autres._");
            }
            event.getHook().sendMessage(body.toString()).queue();
        } catch (Exception ex) {
            log.warn("Échec /triggers: {}", ex.getMessage());
            event.getHook().sendMessage("Impossible de lister les triggers.").queue();
        }
    }

    private void handleStats(SlashCommandInteractionEvent event) {
        if (!requireGuild(event)) {
            return;
        }
        event.deferReply(false).queue();
        try {
            BotStatsDto stats = statsService.snapshot(event.getGuild().getId());
            StringBuilder body = new StringBuilder();
            body.append("**Stats DidiBot** — ").append(event.getGuild().getName()).append("\n");
            body.append("Aujourd’hui : **").append(stats.getRepliesToday()).append("** réponses\n");
            body.append("7 jours : **").append(stats.getRepliesLast7Days()).append("**\n");
            body.append("Total : **").append(stats.getRepliesAllTime()).append("** · règles actives : **")
                    .append(stats.getActiveTriggers()).append("**\n");
            appendRanking(body, "Top triggers", stats.getTopTriggers());
            appendRanking(body, "Top trolleurs", stats.getTopUsers());
            event.getHook().sendMessage(clip(body.toString())).queue();
        } catch (Exception ex) {
            log.warn("Échec /stats: {}", ex.getMessage());
            event.getHook().sendMessage("Impossible de charger les stats.").queue();
        }
    }

    private void handlePropose(SlashCommandInteractionEvent event) {
        if (!requireGuild(event)) {
            return;
        }

        event.deferReply(true).queue();

        try {
            String name = required(event, "nom");
            String pattern = required(event, "motif");
            String response = required(event, "reponse");
            String typeValue = optional(event, "type", "CONTAINS");

            TriggerProposeRequest request = new TriggerProposeRequest();
            request.setName(name);
            request.setPattern(pattern);
            request.setType(TriggerType.valueOf(typeValue.toUpperCase()));
            request.setResponses(List.of(response));
            request.setDiscordGuildId(event.getGuild().getId());
            request.setProposedBy(event.getUser().getName());
            request.setCooldownSeconds(30);

            var created = triggerService.propose(request, event.getUser().getId());
            event.getHook().sendMessage(
                    "✅ Proposition #" + created.getId() + " enregistrée. Un admin doit l’approuver dans le panel."
            ).queue();
        } catch (Exception ex) {
            log.warn("Échec propose-trigger: {}", ex.getMessage());
            event.getHook().sendMessage("❌ Impossible d’enregistrer la proposition : " + ex.getMessage()).queue();
        }
    }

    private boolean requireGuild(SlashCommandInteractionEvent event) {
        if (event.isFromGuild() && event.getGuild() != null) {
            return true;
        }
        event.reply("Cette commande doit être utilisée sur un serveur.").setEphemeral(true).queue();
        return false;
    }

    private static void appendRanking(StringBuilder body, String title, List<NameCountDto> rows) {
        body.append("\n**").append(title).append("**\n");
        if (rows == null || rows.isEmpty()) {
            body.append("_Pas encore de données._\n");
            return;
        }
        int rank = 1;
        for (NameCountDto row : rows) {
            body.append(rank++).append(". ").append(row.name()).append(" — ").append(row.count()).append("\n");
        }
    }

    private static String clip(String text) {
        if (text.length() <= DISCORD_MAX) {
            return text;
        }
        return text.substring(0, DISCORD_MAX - 1) + "…";
    }

    private String required(SlashCommandInteractionEvent event, String name) {
        OptionMapping option = event.getOption(name);
        if (option == null || option.getAsString().isBlank()) {
            throw new IllegalArgumentException("Option manquante: " + name);
        }
        return option.getAsString().trim();
    }

    private String optional(SlashCommandInteractionEvent event, String name, String defaultValue) {
        OptionMapping option = event.getOption(name);
        return option == null || option.getAsString().isBlank() ? defaultValue : option.getAsString().trim();
    }
}
