package ru.practicum.stats_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats_service.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.info("Пришел POST запрос на добавление hit {}", endpointHitDto);
        service.add(endpointHitDto);
        log.info("Hit добавлен");
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> get(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                  @RequestParam(required = false) List<String> uris,
                                  @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("Пришел GET запрос на получение списка просмотров с параметрами {}, {}, {}, {}", start, end, uris, unique);
        List<ViewStatsDto> viewStats = service.get(start, end, uris, unique);
        log.info("Ответ отправлен {}", viewStats);
        return viewStats;
    }
}
