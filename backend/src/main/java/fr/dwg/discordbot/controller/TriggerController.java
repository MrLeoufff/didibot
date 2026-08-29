package fr.dwg.discordbot.controller;

import fr.dwg.discordbot.dto.ResponseCreateRequest;
import fr.dwg.discordbot.dto.TriggerDto;
import fr.dwg.discordbot.dto.TriggerProposeRequest;
import fr.dwg.discordbot.dto.TriggerRequest;
import fr.dwg.discordbot.dto.TriggerResponseDto;
import fr.dwg.discordbot.service.TriggerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/triggers")
public class TriggerController {

    private final TriggerService triggerService;

    public TriggerController(TriggerService triggerService) {
        this.triggerService = triggerService;
    }

    @GetMapping
    public List<TriggerDto> findAll() {
        return triggerService.findAll();
    }

    @GetMapping("/pending")
    public List<TriggerDto> findPending() {
        return triggerService.findPending();
    }

    @GetMapping("/{id}")
    public TriggerDto findById(@PathVariable Long id) {
        return triggerService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TriggerDto create(@Valid @RequestBody TriggerRequest request) {
        return triggerService.create(request);
    }

    @PostMapping("/propose")
    @ResponseStatus(HttpStatus.CREATED)
    public TriggerDto propose(@Valid @RequestBody TriggerProposeRequest request) {
        return triggerService.propose(request, null);
    }

    @PutMapping("/{id}")
    public TriggerDto update(@PathVariable Long id, @Valid @RequestBody TriggerRequest request) {
        return triggerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        triggerService.delete(id);
    }

    @PatchMapping("/{id}/enable")
    public TriggerDto enable(@PathVariable Long id) {
        return triggerService.setEnabled(id, true);
    }

    @PatchMapping("/{id}/disable")
    public TriggerDto disable(@PathVariable Long id) {
        return triggerService.setEnabled(id, false);
    }

    @PatchMapping("/{id}/approve")
    public TriggerDto approve(@PathVariable Long id) {
        return triggerService.approve(id);
    }

    @PatchMapping("/{id}/reject")
    public TriggerDto reject(@PathVariable Long id) {
        return triggerService.reject(id);
    }

    @PostMapping("/{id}/responses")
    @ResponseStatus(HttpStatus.CREATED)
    public TriggerResponseDto addResponse(
            @PathVariable Long id,
            @Valid @RequestBody ResponseCreateRequest request
    ) {
        return triggerService.addResponse(id, request);
    }
}
