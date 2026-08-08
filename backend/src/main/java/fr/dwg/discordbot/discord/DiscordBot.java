package fr.dwg.discordbot.discord;

import fr.dwg.discordbot.config.DiscordProperties;
import fr.dwg.discordbot.service.ServerService;
import jakarta.annotation.PreDestroy;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DiscordBot {

    private static final Logger log = LoggerFactory.getLogger(DiscordBot.class);

    private final DiscordProperties discordProperties;
    private final DiscordMessageListener discordMessageListener;
    private final ServerService serverService;

    private JDA jda;

    public DiscordBot(
            DiscordProperties discordProperties,
            DiscordMessageListener discordMessageListener,
            ServerService serverService
    ) {
        this.discordProperties = discordProperties;
        this.discordMessageListener = discordMessageListener;
        this.serverService = serverService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        if (!discordProperties.isEnabled()) {
            log.warn("Bot Discord désactivé (discord.enabled=false)");
            return;
        }

        String token = discordProperties.getToken();
        if (token == null || token.isBlank()) {
            log.warn("DISCORD_TOKEN manquant : le bot ne se connectera pas. L'API REST reste disponible.");
            return;
        }

        try {
            log.info("Connexion du bot Discord...");
            jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .setMemberCachePolicy(MemberCachePolicy.NONE)
                    .setChunkingFilter(ChunkingFilter.NONE)
                    .setActivity(Activity.watching("les triggers"))
                    .addEventListeners(discordMessageListener)
                    .build()
                    .awaitReady();

            for (Guild guild : jda.getGuilds()) {
                serverService.syncGuild(guild.getId(), guild.getName());
                log.info("Serveur synchronisé : {} ({})", guild.getName(), guild.getId());
            }

            log.info("Bot Discord connecté en tant que {}", jda.getSelfUser().getName());
        } catch (Exception ex) {
            log.error(
                    "Échec de connexion Discord (API REST toujours disponible). "
                            + "Vérifie le token et active Message Content Intent "
                            + "dans le Developer Portal → Bot → Privileged Gateway Intents.",
                    ex
            );
            if (jda != null) {
                jda.shutdownNow();
                jda = null;
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        if (jda != null) {
            log.info("Arrêt du bot Discord...");
            jda.shutdown();
        }
    }

    public JDA getJda() {
        return jda;
    }
}
