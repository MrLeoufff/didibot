package fr.dwg.discordbot.service;

import fr.dwg.discordbot.config.DiscordProperties;
import fr.dwg.discordbot.discord.DiscordBot;
import fr.dwg.discordbot.dto.TriggerDto;
import fr.dwg.discordbot.event.TriggerProposedEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class DiscordNotifyService {

    private static final Logger log = LoggerFactory.getLogger(DiscordNotifyService.class);

    private final DiscordProperties discordProperties;
    private final DiscordBot discordBot;

    public DiscordNotifyService(DiscordProperties discordProperties, @Lazy DiscordBot discordBot) {
        this.discordProperties = discordProperties;
        this.discordBot = discordBot;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTriggerProposed(TriggerProposedEvent event) {
        notifyProposal(event.trigger());
    }

    public void notifyProposal(TriggerDto trigger) {
        if (trigger == null) {
            return;
        }
        String channelId = discordProperties.getAdminChannelId();
        if (channelId == null || channelId.isBlank()) {
            return;
        }
        JDA jda = discordBot.getJda();
        if (jda == null) {
            log.debug("Proposition #{} : JDA hors ligne, notif ignorée", trigger.getId());
            return;
        }
        TextChannel channel = jda.getTextChannelById(channelId.trim());
        if (channel == null) {
            log.warn("Salon admin introuvable: {}", channelId);
            return;
        }

        String proposedBy = trigger.getProposedBy() == null || trigger.getProposedBy().isBlank()
                ? "Anonyme"
                : trigger.getProposedBy();
        String mention = trigger.getProposedByDiscordId() == null || trigger.getProposedByDiscordId().isBlank()
                ? proposedBy
                : "<@" + trigger.getProposedByDiscordId() + ">";
        String server = trigger.getDiscordServerName() == null ? "?" : trigger.getDiscordServerName();

        String message = "**Nouvelle proposition #" + trigger.getId() + "**\n"
                + "Par " + mention + " · serveur **" + server + "**\n"
                + "• " + trigger.getName() + " — `" + trigger.getPattern() + "` (" + trigger.getType() + ")\n"
                + "À valider dans le panel admin.";

        channel.sendMessage(message).queue(
                success -> { },
                error -> log.warn("Impossible d’envoyer la notif de proposition: {}", error.getMessage())
        );
    }
}
