package fr.dwg.discordbot.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class BotImageService {

    private static final Logger log = LoggerFactory.getLogger(BotImageService.class);
    private static final String RESOURCE_PATH = "didibot-avatar.png";

    private byte[] imageBytes = new byte[0];

    @PostConstruct
    void load() {
        try {
            ClassPathResource resource = new ClassPathResource(RESOURCE_PATH);
            if (!resource.exists()) {
                log.warn("Image DidiBot introuvable dans le classpath ({})", RESOURCE_PATH);
                return;
            }
            try (InputStream in = resource.getInputStream()) {
                imageBytes = in.readAllBytes();
            }
            log.info("Image DidiBot chargée ({} octets)", imageBytes.length);
        } catch (IOException ex) {
            log.error("Impossible de charger l'image DidiBot", ex);
            imageBytes = new byte[0];
        }
    }

    public boolean isAvailable() {
        return imageBytes.length > 0;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public InputStream openStream() {
        return new java.io.ByteArrayInputStream(imageBytes);
    }
}
