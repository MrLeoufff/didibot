package fr.dwg.discordbot.service;

import fr.dwg.discordbot.dto.ProcessedReply;
import fr.dwg.discordbot.entity.Trigger;
import fr.dwg.discordbot.entity.TriggerAction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageProcessingServiceTest {

    @Test
    void neverMissesWhenChanceIsOne() {
        Trigger trigger = new Trigger();
        trigger.setFireChance(1.0);
        assertFalse(MessageProcessingService.missesFireChance(trigger));
    }

    @Test
    void alwaysMissesWhenChanceIsZero() {
        Trigger trigger = new Trigger();
        trigger.setFireChance(0.0);
        assertTrue(MessageProcessingService.missesFireChance(trigger));
    }

    @Test
    void reactOnlyDoesNotSendAMessage() {
        Trigger trigger = new Trigger();
        trigger.setId(7L);
        trigger.setName("Eyes");
        trigger.setAction(TriggerAction.REACT);
        trigger.setReactionEmoji("👀");
        ProcessedReply reply = MessageProcessingService.toProcessed(trigger, "ignored", true);
        assertFalse(reply.sendMessage());
        assertFalse(reply.attachImage());
        assertEquals("👀", reply.reactionEmoji());
    }

    @Test
    void replyDoesNotAttachAReaction() {
        Trigger trigger = new Trigger();
        trigger.setId(8L);
        trigger.setName("Talk");
        trigger.setAction(TriggerAction.REPLY);
        trigger.setReactionEmoji("👀");
        ProcessedReply reply = MessageProcessingService.toProcessed(trigger, "hello", false);
        assertTrue(reply.sendMessage());
        assertNull(reply.reactionEmoji());
        assertEquals("hello", reply.responseContent());
    }
}
