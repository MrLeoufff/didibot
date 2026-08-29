package fr.dwg.discordbot.discord;

import fr.dwg.discordbot.dto.IncomingMessage;
import fr.dwg.discordbot.dto.ProcessedReply;
import fr.dwg.discordbot.service.BotImageService;
import fr.dwg.discordbot.service.GifAlertService;
import fr.dwg.discordbot.service.MessageDedupService;
import fr.dwg.discordbot.service.MessageProcessingService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DiscordMessageListener extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(DiscordMessageListener.class);

    private final MessageProcessingService messageProcessingService;
    private final MessageDedupService messageDedupService;
    private final BotImageService botImageService;
    private final GifAlertService gifAlertService;

    public DiscordMessageListener(
            MessageProcessingService messageProcessingService,
            MessageDedupService messageDedupService,
            BotImageService botImageService,
            GifAlertService gifAlertService
    ) {
        this.messageProcessingService = messageProcessingService;
        this.messageDedupService = messageDedupService;
        this.botImageService = botImageService;
        this.gifAlertService = gifAlertService;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!isProcessable(event.getAuthor().isBot(), event.isWebhookMessage(), event.isFromGuild())) {
            return;
        }

        Guild guild = event.getGuild();
        MessageChannel channel = event.getChannel();
        IncomingMessage incoming = toIncoming(event.getMessage(), guild, channel);

        try {
            Optional<ProcessedReply> gifReply = gifAlertService.maybeReply(incoming, event.getMessage());
            if (gifReply.isPresent()) {
                if (messageDedupService.alreadyProcessed("gif:" + event.getMessageId())) {
                    return;
                }
                log.info("Alerte GIF déclenchée par {} dans #{}", event.getAuthor().getName(), channel.getName());
                sendOutcome(event.getMessage(), gifReply.get());
                return;
            }

            if (messageDedupService.alreadyProcessed(event.getMessageId())) {
                return;
            }

            Optional<ProcessedReply> reply = messageProcessingService.process(incoming);
            reply.ifPresent(processed -> sendOutcome(event.getMessage(), processed));
        } catch (Exception ex) {
            log.error("Erreur lors du traitement du message Discord", ex);
        }
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        if (!isProcessable(event.getAuthor().isBot(), event.getMessage().isWebhookMessage(), event.isFromGuild())) {
            return;
        }
        if (messageDedupService.alreadyProcessed("gif:" + event.getMessageId())) {
            return;
        }
        try {
            IncomingMessage incoming = toIncoming(event.getMessage(), event.getGuild(), event.getChannel());
            Optional<ProcessedReply> gifReply = gifAlertService.maybeReply(incoming, event.getMessage());
            if (gifReply.isEmpty()) {
                return;
            }
            messageDedupService.alreadyProcessed("gif:" + event.getMessageId());
            log.info(
                    "Alerte GIF (embed) déclenchée par {} dans #{}",
                    event.getAuthor().getName(),
                    event.getChannel().getName()
            );
            sendOutcome(event.getMessage(), gifReply.get());
        } catch (Exception ex) {
            log.error("Erreur lors du traitement GIF Discord", ex);
        }
    }

    private boolean isProcessable(boolean bot, boolean webhook, boolean fromGuild) {
        return !bot && !webhook && fromGuild;
    }

    private IncomingMessage toIncoming(
            net.dv8tion.jda.api.entities.Message message,
            Guild guild,
            MessageChannel channel
    ) {
        return new IncomingMessage(
                guild.getId(),
                guild.getName(),
                channel.getId(),
                channel.getName(),
                message.getAuthor().getId(),
                message.getAuthor().getName(),
                message.getContentRaw()
        );
    }

    private void sendOutcome(net.dv8tion.jda.api.entities.Message message, ProcessedReply processed) {
        if (processed.reactionEmoji() != null && !processed.reactionEmoji().isBlank()) {
            try {
                message.addReaction(Emoji.fromFormatted(processed.reactionEmoji())).queue(
                        success -> { },
                        error -> log.warn("Réaction impossible ({}): {}", processed.reactionEmoji(), error.getMessage())
                );
            } catch (RuntimeException ex) {
                log.warn("Emoji invalide '{}': {}", processed.reactionEmoji(), ex.getMessage());
            }
        }

        if (!processed.sendMessage() || processed.responseContent() == null || processed.responseContent().isBlank()) {
            return;
        }

        MessageCreateBuilder builder = new MessageCreateBuilder()
                .setContent(processed.responseContent());

        if (processed.attachImage() && botImageService.isAvailable()) {
            builder.addFiles(FileUpload.fromData(botImageService.getImageBytes(), "didibot.png"));
        }

        message.reply(builder.build()).queue(
                success -> { },
                error -> log.error("Échec d'envoi de la réponse Discord", error)
        );
    }
}
