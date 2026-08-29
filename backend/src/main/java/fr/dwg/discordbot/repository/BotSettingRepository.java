package fr.dwg.discordbot.repository;

import fr.dwg.discordbot.entity.BotSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BotSettingRepository extends JpaRepository<BotSetting, String> {
}
