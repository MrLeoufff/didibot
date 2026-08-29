package fr.dwg.discordbot.service;

import fr.dwg.discordbot.entity.Trigger;
import fr.dwg.discordbot.entity.TriggerType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TriggerScopeServiceTest {

    private final TriggerScopeService service = new TriggerScopeService();

    @Test
    void usesGlobalWhenLocalIsEmpty() {
        Trigger global = trigger(1L, TriggerType.CONTAINS, "php");
        List<Trigger> merged = service.mergeLocalAndGlobal(List.of(), List.of(global));
        assertEquals(1, merged.size());
        assertEquals(1L, merged.getFirst().getId());
    }

    @Test
    void localPatternOverridesGlobalSamePattern() {
        Trigger local = trigger(10L, TriggerType.CONTAINS, "Java");
        Trigger globalSame = trigger(20L, TriggerType.CONTAINS, "java");
        Trigger globalExtra = trigger(21L, TriggerType.CONTAINS, "rust");

        List<Trigger> merged = service.mergeLocalAndGlobal(List.of(local), List.of(globalSame, globalExtra));
        assertEquals(2, merged.size());
        assertEquals(10L, merged.getFirst().getId());
        assertTrue(merged.stream().anyMatch(t -> t.getId() == 21L));
        assertTrue(merged.stream().noneMatch(t -> t.getId() == 20L));
    }

    @Test
    void differentTypesAreNotDuplicates() {
        Trigger local = trigger(1L, TriggerType.EXACT, "go");
        Trigger global = trigger(2L, TriggerType.CONTAINS, "go");
        List<Trigger> merged = service.mergeLocalAndGlobal(List.of(local), List.of(global));
        assertEquals(2, merged.size());
    }

    private Trigger trigger(Long id, TriggerType type, String pattern) {
        Trigger trigger = new Trigger();
        trigger.setId(id);
        trigger.setType(type);
        trigger.setPattern(pattern);
        return trigger;
    }
}
