package fr.dwg.discordbot.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageDedupService {

    private static final long TTL_SECONDS = 120;
    private final Map<String, Instant> seen = new ConcurrentHashMap<>();

    public boolean alreadyProcessed(String messageId) {
        cleanup();
        Instant previous = seen.putIfAbsent(messageId, Instant.now());
        return previous != null;
    }

    private void cleanup() {
        Instant threshold = Instant.now().minusSeconds(TTL_SECONDS);
        Iterator<Map.Entry<String, Instant>> it = seen.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Instant> entry = it.next();
            if (entry.getValue().isBefore(threshold)) {
                it.remove();
            }
        }
    }
}
