package fr.dwg.discordbot.service;

import fr.dwg.discordbot.entity.Trigger;
import fr.dwg.discordbot.entity.TriggerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatternMatcherServiceTest {

    private PatternMatcherService service;

    @BeforeEach
    void setUp() {
        service = new PatternMatcherService();
    }

    @Test
    void containsMatchesInsideSentence() {
        Trigger trigger = trigger(TriggerType.CONTAINS, "C#");
        assertTrue(service.matches(trigger, "Franchement C# c'est pas mal."));
        assertTrue(service.matches(trigger, "j'aime c#"));
        assertFalse(service.matches(trigger, "Java uniquement"));
    }

    @Test
    void exactMatchesWholeMessage() {
        Trigger trigger = trigger(TriggerType.EXACT, "C#");
        assertTrue(service.matches(trigger, "C#"));
        assertTrue(service.matches(trigger, "c#"));
        assertFalse(service.matches(trigger, "C# rocks"));
    }

    @Test
    void startsWithMatchesPrefix() {
        Trigger trigger = trigger(TriggerType.STARTS_WITH, "ping");
        assertTrue(service.matches(trigger, "ping bot"));
        assertFalse(service.matches(trigger, "bot ping"));
    }

    @Test
    void regexMatchesWordBoundary() {
        Trigger trigger = trigger(TriggerType.REGEX, "(?i)\\bjava\\b");
        assertTrue(service.matches(trigger, "J'aime Java vraiment"));
        assertFalse(service.matches(trigger, "javascript"));
    }

    private Trigger trigger(TriggerType type, String pattern) {
        Trigger trigger = new Trigger();
        trigger.setType(type);
        trigger.setPattern(pattern);
        return trigger;
    }
}
