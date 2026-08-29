package fr.dwg.discordbot.controller;

import fr.dwg.discordbot.dto.TriggerExecutionDto;
import fr.dwg.discordbot.service.TriggerExecutionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final TriggerExecutionService triggerExecutionService;

    public LogController(TriggerExecutionService triggerExecutionService) {
        this.triggerExecutionService = triggerExecutionService;
    }

    @GetMapping
    public Page<TriggerExecutionDto> findAll(
            @RequestParam(required = false) String guildId,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 25, sort = "executedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return triggerExecutionService.findAll(guildId, q, pageable);
    }
}
