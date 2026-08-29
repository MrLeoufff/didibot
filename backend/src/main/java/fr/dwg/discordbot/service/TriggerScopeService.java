package fr.dwg.discordbot.service;

import fr.dwg.discordbot.entity.Trigger;
import fr.dwg.discordbot.entity.TriggerType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class TriggerScopeService {

    public static final String GLOBAL_GUILD_ID = "0";

    /**
     * Les règles du serveur réel priment. Les règles globales (guild 0) s’ajoutent
     * seulement s’il n’existe pas déjà le même type + motif (casse ignorée).
     */
    public List<Trigger> mergeLocalAndGlobal(List<Trigger> local, List<Trigger> global) {
        List<Trigger> safeLocal = local == null ? List.of() : local;
        List<Trigger> safeGlobal = global == null ? List.of() : global;

        Map<String, Trigger> merged = new LinkedHashMap<>();
        for (Trigger trigger : safeLocal) {
            if (trigger != null) {
                merged.put(ruleKey(trigger.getType(), trigger.getPattern()), trigger);
            }
        }
        for (Trigger trigger : safeGlobal) {
            if (trigger == null) {
                continue;
            }
            merged.putIfAbsent(ruleKey(trigger.getType(), trigger.getPattern()), trigger);
        }
        return new ArrayList<>(merged.values());
    }

    public String ruleKey(TriggerType type, String pattern) {
        String normalized = pattern == null ? "" : pattern.trim().toLowerCase(Locale.ROOT);
        String typeName = type == null ? "" : type.name();
        return typeName + "|" + normalized;
    }
}
