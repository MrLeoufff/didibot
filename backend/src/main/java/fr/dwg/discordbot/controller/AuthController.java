package fr.dwg.discordbot.controller;

import fr.dwg.discordbot.config.AdminProperties;
import fr.dwg.discordbot.dto.AppUserDto;
import fr.dwg.discordbot.dto.LoginRequest;
import fr.dwg.discordbot.dto.LoginResponse;
import fr.dwg.discordbot.dto.RegisterRequest;
import fr.dwg.discordbot.entity.AppUser;
import fr.dwg.discordbot.entity.UserStatus;
import fr.dwg.discordbot.exception.BadRequestException;
import fr.dwg.discordbot.security.JwtService;
import fr.dwg.discordbot.service.AppUserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AdminProperties adminProperties;
    private final JwtService jwtService;
    private final AppUserService appUserService;

    public AuthController(
            AdminProperties adminProperties,
            JwtService jwtService,
            AppUserService appUserService
    ) {
        this.adminProperties = adminProperties;
        this.jwtService = jwtService;
        this.appUserService = appUserService;
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AppUserDto register(@Valid @RequestBody RegisterRequest request) {
        return appUserService.register(request);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        String username = request.getUsername() == null ? "" : request.getUsername().trim();
        String password = request.getPassword() == null ? "" : request.getPassword();

        if (adminProperties.getUsername().equals(username)
                && adminProperties.getPassword().equals(password)) {
            String token = jwtService.generateToken(username, "ADMIN");
            return new LoginResponse(token, username, "ADMIN");
        }

        AppUser user = appUserService.findByUsername(username)
                .orElseThrow(() -> invalidCredentials(username));

        if (!appUserService.matchesPassword(user, password)) {
            throw invalidCredentials(username);
        }
        if (user.getStatus() == UserStatus.PENDING) {
            throw new BadRequestException("Compte en attente d’approbation par un admin");
        }
        if (user.getStatus() == UserStatus.REJECTED) {
            throw new BadRequestException("Ce compte a été refusé");
        }

        String token = jwtService.generateToken(user.getUsername(), "USER");
        return new LoginResponse(token, user.getUsername(), "USER");
    }

    private BadRequestException invalidCredentials(String username) {
        log.warn("Échec login pour utilisateur '{}'", username);
        return new BadRequestException("Identifiants invalides");
    }
}
