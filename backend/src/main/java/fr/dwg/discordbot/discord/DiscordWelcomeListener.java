package fr.dwg.discordbot.discord;

import fr.dwg.discordbot.dto.IncomingMessage;
import fr.dwg.discordbot.entity.DiscordServer;
import fr.dwg.discordbot.repository.DiscordServerRepository;
import fr.dwg.discordbot.service.BotSettingsService;
import fr.dwg.discordbot.service.ReplyPlaceholderService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DiscordWelcomeListener extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(DiscordWelcomeListener.class);

    private final DiscordServerRepository discordServerRepository;
    private final ReplyPlaceholderService replyPlaceholderService;

    public DiscordWelcomeListener(
            DiscordServerRepository discordServerRepository,
            ReplyPlaceholderService replyPlaceholderService
    ) {
        this.discordServerRepository = discordServerRepository;
        this.replyPlaceholderService = replyPlaceholderService;
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Member member = event.getMember();
        if (member.getUser().isBot()) {
            return;
        }
        Guild guild = event.getGuild();
        DiscordServer server = discordServerRepository.findByDiscordGuildId(guild.getId()).orElse(null);
        if (server == null || !server.isEnabled() || !server.isWelcomeEnabled()) {
            return;
        }

        TextChannel channel = resolveChannel(guild, server.getWelcomeChannelId());
        if (channel == null) {
            log.warn("Accueil activé sur {} mais salon introuvable", guild.getName());
            return;
        }

        IncomingMessage incoming = new IncomingMessage(
                guild.getId(),
                guild.getName(),
                channel.getId(),
                channel.getName(),
                member.getId(),
                member.getEffectiveName(),
                ""
        );
        String template = server.getWelcomeMessage() == null || server.getWelcomeMessage().isBlank()
                ? BotSettingsService.DEFAULT_WELCOME
                : server.getWelcomeMessage();
        String content = replyPlaceholderService.interpolate(template, incoming);
        if (content == null || content.isBlank()) {
            return;
        }
        channel.sendMessage(content).queue(
                success -> { },
                error -> log.warn("Impossible d'envoyer l'accueil sur {}: {}", guild.getName(), error.getMessage())
        );
    }

    private TextChannel resolveChannel(Guild guild, String channelId) {
        if (channelId != null && !channelId.isBlank()) {
            TextChannel configured = guild.getTextChannelById(channelId.trim());
            if (configured != null) {
                return configured;
            }
        }
        return guild.getSystemChannel();
    }
}
