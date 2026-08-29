package fr.dwg.discordbot.discord;

import fr.dwg.discordbot.config.DiscordProperties;
import fr.dwg.discordbot.service.BotImageService;
import fr.dwg.discordbot.service.ServerService;
import jakarta.annotation.PreDestroy;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
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
    private static final int MAX_CONNECT_ATTEMPTS = 8;
    private static final long RETRY_DELAY_MS = 15_000L;

    private final DiscordProperties discordProperties;
    private final DiscordMessageListener discordMessageListener;
    private final DiscordSlashCommandListener discordSlashCommandListener;
    private final ServerService serverService;
    private final BotImageService botImageService;

    private volatile JDA jda;

    public DiscordBot(
            DiscordProperties discordProperties,
            DiscordMessageListener discordMessageListener,
            DiscordSlashCommandListener discordSlashCommandListener,
            ServerService serverService,
            BotImageService botImageService
    ) {
        this.discordProperties = discordProperties;
        this.discordMessageListener = discordMessageListener;
        this.discordSlashCommandListener = discordSlashCommandListener;
        this.serverService = serverService;
        this.botImageService = botImageService;
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

        for (int attempt = 1; attempt <= MAX_CONNECT_ATTEMPTS; attempt++) {
            try {
                connect(token);
                return;
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                log.warn("Connexion Discord interrompue");
                return;
            } catch (Exception ex) {
                log.error(
                        "Échec de connexion Discord (tentative {}/{}). "
                                + "API REST toujours disponible. Cause: {}",
                        attempt,
                        MAX_CONNECT_ATTEMPTS,
                        ex.getMessage()
                );
                if (jda != null) {
                    jda.shutdownNow();
                    jda = null;
                }
                if (attempt < MAX_CONNECT_ATTEMPTS) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
        log.error(
                "Impossible de connecter Discord après {} tentatives. "
                        + "Vérifie le DNS/réseau du serveur, le token et Message Content Intent.",
                MAX_CONNECT_ATTEMPTS
        );
    }

    private void connect(String token) throws InterruptedException {
        log.info("Connexion du bot Discord...");
        jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .setMemberCachePolicy(MemberCachePolicy.NONE)
                .setChunkingFilter(ChunkingFilter.NONE)
                .setActivity(Activity.playing("Java > C#"))
                .addEventListeners(discordMessageListener, discordSlashCommandListener)
                .build()
                .awaitReady();

        jda.updateCommands().addCommands(
                Commands.slash("propose-trigger", "Proposer un trigger à valider par un admin")
                        .addOption(OptionType.STRING, "nom", "Nom du trigger", true)
                        .addOption(OptionType.STRING, "motif", "Mot ou expression à détecter", true)
                        .addOption(OptionType.STRING, "reponse", "Réponse proposée", true)
                        .addOption(OptionType.STRING, "type", "EXACT, CONTAINS, STARTS_WITH ou REGEX", false)
        ).queue(
                success -> log.info("Commandes slash enregistrées"),
                error -> log.error("Échec d'enregistrement des commandes slash", error)
        );

        for (Guild guild : jda.getGuilds()) {
            serverService.syncGuild(guild.getId(), guild.getName());
            log.info("Serveur synchronisé : {} ({})", guild.getName(), guild.getId());
        }

        updateAvatarIfConfigured();

        log.info("Bot Discord connecté en tant que {}", jda.getSelfUser().getName());
    }

    private void updateAvatarIfConfigured() {
        if (!discordProperties.isUpdateAvatarOnStartup() || !botImageService.isAvailable() || jda == null) {
            return;
        }
        Icon icon = Icon.from(botImageService.getImageBytes());
        jda.getSelfUser().getManager().setAvatar(icon).queue(
                success -> log.info("Avatar DidiBot mis à jour"),
                error -> log.warn("Impossible de mettre à jour l'avatar Discord: {}", error.getMessage())
        );
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
