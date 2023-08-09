package ru.practicum.main_service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.event.dto.EventFullDto;
import ru.practicum.main_service.event.dto.UpdateEventAdminRequest;
import ru.practicum.main_service.event.enums.EventState;
import ru.practicum.main_service.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@Slf4j
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEventsByAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("Пришел GET запрос на получение событий от админа с параметрами: users = {}, states = {}, categories = {}, " +
                "rangeStart = {}, rangeEnd = {}, from = {}, size = {}", users, states, categories, rangeStart, rangeEnd, from, size);
        List<EventFullDto> events = eventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
        log.info("Ответ отправлен: {}", events);
        return events;
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto editEventByAdmin(@PathVariable Long eventId, @Valid @RequestBody UpdateEventAdminRequest request) {
        log.info("Пришел PATCH запрос на измнение данных от админа события с id = {} на данные: {}", eventId, request);
        EventFullDto event = eventService.editEventByAdmin(eventId, request);
        log.info("Ответ отправлен: {}", event);
        return event;
    }
}
