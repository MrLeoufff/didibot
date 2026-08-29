package fr.dwg.discordbot.service;

import fr.dwg.discordbot.config.AdminProperties;
import fr.dwg.discordbot.dto.AppUserDto;
import fr.dwg.discordbot.dto.RegisterRequest;
import fr.dwg.discordbot.entity.AppUser;
import fr.dwg.discordbot.entity.UserStatus;
import fr.dwg.discordbot.exception.BadRequestException;
import fr.dwg.discordbot.exception.ResourceNotFoundException;
import fr.dwg.discordbot.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;

    public AppUserService(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            AdminProperties adminProperties
    ) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminProperties = adminProperties;
    }

    @Transactional
    public AppUserDto register(RegisterRequest request) {
        String username = request.getUsername().trim();
        if (username.equalsIgnoreCase(adminProperties.getUsername())) {
            throw new BadRequestException("Ce nom d’utilisateur est réservé");
        }
        if (appUserRepository.existsByUsernameIgnoreCase(username)) {
            throw new BadRequestException("Ce nom d’utilisateur est déjà pris");
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.PENDING);
        return toDto(appUserRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<AppUserDto> findPending() {
        return appUserRepository.findByStatusOrderByRequestedAtAsc(UserStatus.PENDING).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AppUserDto> findAll() {
        return appUserRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public AppUserDto approve(Long id) {
        AppUser user = getEntity(id);
        if (user.getStatus() != UserStatus.PENDING) {
            throw new BadRequestException("Seuls les comptes en attente peuvent être approuvés");
        }
        user.setStatus(UserStatus.APPROVED);
        user.setReviewedAt(Instant.now());
        return toDto(user);
    }

    @Transactional
    public AppUserDto reject(Long id) {
        AppUser user = getEntity(id);
        if (user.getStatus() != UserStatus.PENDING) {
            throw new BadRequestException("Seuls les comptes en attente peuvent être refusés");
        }
        user.setStatus(UserStatus.REJECTED);
        user.setReviewedAt(Instant.now());
        return toDto(user);
    }

    @Transactional(readOnly = true)
    public Optional<AppUser> findByUsername(String username) {
        return appUserRepository.findByUsernameIgnoreCase(username);
    }

    public boolean matchesPassword(AppUser user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPasswordHash());
    }

    private AppUser getEntity(Long id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte introuvable: " + id));
    }

    private AppUserDto toDto(AppUser user) {
        AppUserDto dto = new AppUserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setStatus(user.getStatus());
        dto.setRequestedAt(user.getRequestedAt());
        dto.setReviewedAt(user.getReviewedAt());
        return dto;
    }
}
