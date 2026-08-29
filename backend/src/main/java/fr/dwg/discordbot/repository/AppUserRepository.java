package fr.dwg.discordbot.repository;

import fr.dwg.discordbot.entity.AppUser;
import fr.dwg.discordbot.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    List<AppUser> findByStatusOrderByRequestedAtAsc(UserStatus status);
}
