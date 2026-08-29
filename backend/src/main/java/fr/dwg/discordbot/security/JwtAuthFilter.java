package fr.dwg.discordbot.security;

import fr.dwg.discordbot.config.AdminProperties;
import fr.dwg.discordbot.entity.UserStatus;
import fr.dwg.discordbot.service.AppUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AdminProperties adminProperties;
    private final AppUserService appUserService;

    public JwtAuthFilter(
            JwtService jwtService,
            AdminProperties adminProperties,
            AppUserService appUserService
    ) {
        this.jwtService = jwtService;
        this.adminProperties = adminProperties;
        this.appUserService = appUserService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                if (jwtService.isValid(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    String username = jwtService.extractUsername(token);
                    String role = jwtService.extractRole(token);
                    if (isAuthorizedUser(username, role)) {
                        var auth = new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (Exception ignored) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isAuthorizedUser(String username, String role) {
        if (username == null || role == null) {
            return false;
        }
        if ("ADMIN".equals(role) && adminProperties.getUsername().equals(username)) {
            return true;
        }
        if ("USER".equals(role)) {
            return appUserService.findByUsername(username)
                    .filter(user -> user.getStatus() == UserStatus.APPROVED)
                    .isPresent();
        }
        return false;
    }
}
