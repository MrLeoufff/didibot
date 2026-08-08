package fr.dwg.discordbot.controller;

import fr.dwg.discordbot.service.TriggerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/responses")
public class ResponseController {

    private final TriggerService triggerService;

    public ResponseController(TriggerService triggerService) {
        this.triggerService = triggerService;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        triggerService.deleteResponse(id);
    }
}
