package fr.dwg.discordbot.discord;

import fr.dwg.discordbot.dto.TriggerProposeRequest;
import fr.dwg.discordbot.entity.TriggerType;
import fr.dwg.discordbot.service.TriggerService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DiscordSlashCommandListener extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(DiscordSlashCommandListener.class);

    private final TriggerService triggerService;

    public DiscordSlashCommandListener(TriggerService triggerService) {
        this.triggerService = triggerService;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!"propose-trigger".equals(event.getName())) {
            return;
        }
        if (!event.isFromGuild() || event.getGuild() == null) {
            event.reply("Cette commande doit être utilisée sur un serveur.").setEphemeral(true).queue();
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
