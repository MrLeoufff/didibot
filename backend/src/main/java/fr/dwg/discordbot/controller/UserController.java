package fr.dwg.discordbot.controller;

import fr.dwg.discordbot.dto.AppUserDto;
import fr.dwg.discordbot.service.AppUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AppUserService appUserService;

    public UserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping
    public List<AppUserDto> findAll() {
        return appUserService.findAll();
    }

    @GetMapping("/pending")
    public List<AppUserDto> findPending() {
        return appUserService.findPending();
    }

    @PatchMapping("/{id}/approve")
    public AppUserDto approve(@PathVariable Long id) {
        return appUserService.approve(id);
    }

    @PatchMapping("/{id}/reject")
    public AppUserDto reject(@PathVariable Long id) {
        return appUserService.reject(id);
    }
}
