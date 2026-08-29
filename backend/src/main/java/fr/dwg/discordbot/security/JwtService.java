package fr.dwg.discordbot.security;

import fr.dwg.discordbot.config.AdminProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final AdminProperties adminProperties;
    private final SecretKey key;

    public JwtService(AdminProperties adminProperties) {
        this.adminProperties = adminProperties;
        byte[] secret = adminProperties.getJwtSecret().getBytes(StandardCharsets.UTF_8);
        if (secret.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(secret, 0, padded, 0, Math.min(secret.length, 32));
            secret = padded;
        }
        this.key = Keys.hmacShaKeyFor(secret);
    }

    public String generateToken(String username, String role) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(adminProperties.getJwtExpirationMinutes() * 60);
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractRole(String token) {
        Object role = parseClaims(token).get("role");
        return role == null ? null : role.toString();
    }

    public boolean isValid(String token) {
        Claims claims = parseClaims(token);
        return claims.getExpiration().after(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
