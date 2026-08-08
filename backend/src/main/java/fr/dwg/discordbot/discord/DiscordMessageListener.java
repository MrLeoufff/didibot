package fr.dwg.discordbot.discord;

import fr.dwg.discordbot.dto.IncomingMessage;
import fr.dwg.discordbot.dto.ProcessedReply;
import fr.dwg.discordbot.service.MessageProcessingService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DiscordMessageListener extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(DiscordMessageListener.class);

    private final MessageProcessingService messageProcessingService;

    public DiscordMessageListener(MessageProcessingService messageProcessingService) {
        this.messageProcessingService = messageProcessingService;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.isWebhookMessage()) {
            return;
        }
        if (!event.isFromGuild()) {
            return;
        }

        Guild guild = event.getGuild();
        MessageChannel channel = event.getChannel();

        IncomingMessage message = new IncomingMessage(
                guild.getId(),
                guild.getName(),
                channel.getId(),
                channel.getName(),
                event.getAuthor().getId(),
                event.getAuthor().getName(),
                event.getMessage().getContentRaw()
        );

        try {
            Optional<ProcessedReply> reply = messageProcessingService.process(message);
            reply.ifPresent(processed -> event.getMessage().reply(processed.responseContent()).queue(
                    success -> { },
                    error -> log.error("Échec d'envoi de la réponse Discord", error)
            ));
        } catch (Exception ex) {
            log.error("Erreur lors du traitement du message Discord", ex);
        }
    }
}
