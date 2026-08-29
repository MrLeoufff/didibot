package fr.dwg.discordbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "admin")
public class AdminProperties {

    private String username = "admin";
    private String password = "changeme";
    private String jwtSecret = "change-me-to-a-long-random-secret-key-32chars-min";
    private long jwtExpirationMinutes = 720;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = stripQuotes(username);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = stripQuotes(password);
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = stripQuotes(jwtSecret);
    }

    public long getJwtExpirationMinutes() {
        return jwtExpirationMinutes;
    }

    public void setJwtExpirationMinutes(long jwtExpirationMinutes) {
        this.jwtExpirationMinutes = jwtExpirationMinutes;
    }

    private static String stripQuotes(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() >= 2) {
            char first = trimmed.charAt(0);
            char last = trimmed.charAt(trimmed.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return trimmed.substring(1, trimmed.length() - 1);
            }
        }
        return trimmed;
    }
}
