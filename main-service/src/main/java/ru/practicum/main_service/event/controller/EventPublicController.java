package ru.practicum.main_service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.event.dto.EventFullDto;
import ru.practicum.main_service.event.dto.EventShortDto;
import ru.practicum.main_service.event.enums.EventTypes;
import ru.practicum.main_service.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Slf4j
public class EventPublicController {
    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsByPublic(@RequestParam(required = false) String text,
                                                 @RequestParam(required = false) List<Long> categories,
                                                 @RequestParam(required = false) Boolean paid,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                 @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                                 @RequestParam(required = false) EventTypes sort,
                                                 @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(required = false, defaultValue = "10") @Positive Integer size,
                                                 HttpServletRequest request) {
        log.error("Пришел GET публичный запрос с параметрами: text = {}, categories = {}, paid = {}, rangeStart = {}, rangeEnd = {}, " +
                "onlyAvailable = {}, sort = {}, from = {}, size = {}", text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        List<EventShortDto> events = eventService.getEventsByPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
        log.info("Ответ отправлен: {}", events);
        return events;
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByIdByPublic(@PathVariable Long id,
                                             HttpServletRequest request) {
        log.info("Пришел GET публичный запрос на получение события с id = {}", id);
        EventFullDto event = eventService.getEventByIdByPublic(id, request);
        log.info("Ответ отправлен: {}", event);
        return event;
    }
}
