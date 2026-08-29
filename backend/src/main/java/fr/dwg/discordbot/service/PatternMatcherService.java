package fr.dwg.discordbot.service;

import fr.dwg.discordbot.entity.Trigger;
import fr.dwg.discordbot.entity.TriggerType;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Service
public class PatternMatcherService {

    public boolean matches(Trigger trigger, String messageContent) {
        if (messageContent == null || messageContent.isBlank()) {
            return false;
        }

        String content = messageContent.trim();
        String pattern = trigger.getPattern();

        return switch (trigger.getType()) {
            case EXACT -> content.equalsIgnoreCase(pattern);
            case CONTAINS -> containsIgnoreCase(content, pattern);
            case STARTS_WITH -> content.regionMatches(true, 0, pattern, 0, pattern.length());
            case REGEX -> matchesRegex(pattern, content);
        };
    }

    private boolean containsIgnoreCase(String content, String pattern) {
        return content.toLowerCase(Locale.ROOT).contains(pattern.toLowerCase(Locale.ROOT));
    }

    private boolean matchesRegex(String regex, String content) {
        try {
            return Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)
                    .matcher(content)
                    .find();
        } catch (PatternSyntaxException ex) {
            return false;
        }
    }

    public boolean isValidPattern(TriggerType type, String pattern) {
        if (type != TriggerType.REGEX) {
            return pattern != null && !pattern.isBlank();
        }
        try {
            Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            return true;
        } catch (PatternSyntaxException ex) {
            return false;
        }
    }
}
