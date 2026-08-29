package fr.dwg.discordbot.service;

import fr.dwg.discordbot.config.DiscordProperties;
import fr.dwg.discordbot.dto.BotSettingsDto;
import fr.dwg.discordbot.dto.WelcomeSettingsDto;
import fr.dwg.discordbot.entity.BotSetting;
import fr.dwg.discordbot.entity.DiscordServer;
import fr.dwg.discordbot.repository.BotSettingRepository;
import fr.dwg.discordbot.repository.DiscordServerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class BotSettingsService {

    public static final String KEY_AVATAR = "avatar_image_chance";
    public static final String KEY_RARE = "rare_event_chance";
    public static final String KEY_ADMIN_CHANNEL = "admin_channel_id";
    public static final String DEFAULT_WELCOME = "Bienvenue {mention}. DidiBot t'a à l'œil.";

    private final BotSettingRepository botSettingRepository;
    private final DiscordServerRepository discordServerRepository;
    private final DiscordProperties discordProperties;

    public BotSettingsService(
            BotSettingRepository botSettingRepository,
            DiscordServerRepository discordServerRepository,
            DiscordProperties discordProperties
    ) {
        this.botSettingRepository = botSettingRepository;
        this.discordServerRepository = discordServerRepository;
        this.discordProperties = discordProperties;
    }

    @Transactional(readOnly = true)
    public BotSettingsDto get() {
        BotSettingsDto dto = new BotSettingsDto();
        dto.setAvatarImageChance(getAvatarImageChance());
        dto.setRareEventChance(getRareEventChance());
        dto.setAdminChannelId(getAdminChannelId());
        dto.setServers(discordServerRepository.findAll().stream()
                .filter(server -> !TriggerScopeService.GLOBAL_GUILD_ID.equals(server.getDiscordGuildId()))
                .map(this::toWelcome)
                .toList());
        return dto;
    }

    @Transactional
    public BotSettingsDto save(BotSettingsDto request) {
        if (request.getAvatarImageChance() != null) {
            put(KEY_AVATAR, formatChance(request.getAvatarImageChance()));
        }
        if (request.getRareEventChance() != null) {
            put(KEY_RARE, formatChance(request.getRareEventChance()));
        }
        if (request.getAdminChannelId() != null) {
            put(KEY_ADMIN_CHANNEL, request.getAdminChannelId().trim());
        }
        if (request.getServers() != null) {
            for (WelcomeSettingsDto welcome : request.getServers()) {
                applyWelcome(welcome);
            }
        }
        return get();
    }

    @Transactional(readOnly = true)
    public double getAvatarImageChance() {
        return readChance(KEY_AVATAR, discordProperties.getAvatarImageChance());
    }

    @Transactional(readOnly = true)
    public double getRareEventChance() {
        return readChance(KEY_RARE, discordProperties.getRareEventChance());
    }

    @Transactional(readOnly = true)
    public String getAdminChannelId() {
        return botSettingRepository.findById(KEY_ADMIN_CHANNEL)
                .map(BotSetting::getValue)
                .filter(value -> value != null && !value.isBlank())
                .orElseGet(() -> nullToEmpty(discordProperties.getAdminChannelId()));
    }

    private void applyWelcome(WelcomeSettingsDto welcome) {
        if (welcome == null || welcome.getServerId() == null) {
            return;
        }
        DiscordServer server = discordServerRepository.findById(welcome.getServerId()).orElse(null);
        if (server == null || TriggerScopeService.GLOBAL_GUILD_ID.equals(server.getDiscordGuildId())) {
            return;
        }
        server.setWelcomeEnabled(welcome.isWelcomeEnabled());
        server.setWelcomeChannelId(blankToNull(welcome.getWelcomeChannelId()));
        String message = welcome.getWelcomeMessage();
        server.setWelcomeMessage(message == null || message.isBlank() ? null : message.trim());
    }

    private WelcomeSettingsDto toWelcome(DiscordServer server) {
        WelcomeSettingsDto dto = new WelcomeSettingsDto();
        dto.setServerId(server.getId());
        dto.setName(server.getName());
        dto.setDiscordGuildId(server.getDiscordGuildId());
        dto.setWelcomeEnabled(server.isWelcomeEnabled());
        dto.setWelcomeChannelId(server.getWelcomeChannelId());
        dto.setWelcomeMessage(server.getWelcomeMessage() == null || server.getWelcomeMessage().isBlank()
                ? DEFAULT_WELCOME
                : server.getWelcomeMessage());
        return dto;
    }

    private void put(String key, String value) {
        BotSetting setting = botSettingRepository.findById(key).orElseGet(BotSetting::new);
        setting.setKey(key);
        setting.setValue(value == null ? "" : value);
        botSettingRepository.save(setting);
    }

    private double readChance(String key, double fallback) {
        return botSettingRepository.findById(key)
                .map(BotSetting::getValue)
                .filter(value -> value != null && !value.isBlank())
                .map(this::parseChance)
                .orElse(clamp(fallback));
    }

    private double parseChance(String raw) {
        try {
            return clamp(Double.parseDouble(raw.trim().replace(',', '.')));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private static String formatChance(double value) {
        return String.format(Locale.US, "%.4f", clamp(value));
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
