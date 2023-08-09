package ru.practicum.main_service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.event.dto.*;
import ru.practicum.main_service.event.service.EventService;
import ru.practicum.main_service.event.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Slf4j
public class EventUserController {
    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllEventsByUser(@PathVariable Long userId,
                                                  @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                                  @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("Пришел GET запрос на получение всех событий от пользователя с id = {} и параметрами: from = {}, size = {}",
                userId, from, size);
        List<EventShortDto> events = eventService.getAllEventsByUser(userId, PageRequest.of(from / size, size));
        log.info("Ответ отправлен: {}", events);
        return events;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEventByUser(@PathVariable Long userId, @Valid @RequestBody NewEventDto event) {
        log.info("Пришел POST запрос от пользователя с id = {} на создание события: {}", userId, event);
        EventFullDto responseEvent = eventService.createEventByUser(userId, event);
        log.info("Ответ отправлен: {}", responseEvent);
        return responseEvent;
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByUser(@PathVariable Long userId,
                                       @PathVariable Long eventId) {
        log.info("Пришел GET запрос от пользователя с id = {} на получение события с id = {}", userId, eventId);
        EventFullDto event = eventService.getEventByUser(userId, eventId);
        log.info("Ответ отправлен: {}", event);
        return event;
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto editEventByUser(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @Valid @RequestBody UpdateEventUserRequest request) {
        log.info("Пришел PATCH запрос от пользователя с id = {} на измнение данных события с id = {} на данные: {}", userId, eventId, request);
        EventFullDto event = eventService.editEventByUser(userId, eventId, request);
        log.info("Ответ отправлен: {}", event);
        return event;
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventRequestsByEventOwner(@PathVariable Long userId,
                                                                      @PathVariable Long eventId) {
        log.info("Пришел GET запрос от пользователя с id = {} на получение его запросов где id события = {}", userId, eventId);
        List<ParticipationRequestDto> requests = requestService.getEventRequestsByEventOwner(userId, eventId);
        log.info("Ответ отправлен: {}", requests);
        return requests;
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult editEventRequestsByEventOwner(@PathVariable Long userId,
                                                                        @PathVariable Long eventId,
                                                                        @Valid @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("Пришел PATCH запрос от пользователя с id = {} на измнение статусов события с id = {} на: {}", userId, eventId, request);
        EventRequestStatusUpdateResult result = requestService.editEventRequestsByEventOwner(userId, eventId, request);
        log.info("Ответ отправлен: {}", result);
        return result;
    }

}
