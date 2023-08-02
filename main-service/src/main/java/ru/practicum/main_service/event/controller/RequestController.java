package ru.practicum.main_service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.event.dto.ParticipationRequestDto;
import ru.practicum.main_service.event.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("users/{userId}/requests")
@Slf4j
public class RequestController {
    private final RequestService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventRequestsByRequester(@PathVariable Long userId) {
        log.info("Пришел GET запрос на получение личных запросов от пользователя с id = {}", userId);
        List<ParticipationRequestDto> requests = requestService.getEventRequestsByRequester(userId);
        log.info("Ответ отправлен: {}", requests);
        return requests;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createEventRequest(@PathVariable Long userId,
                                                      @RequestParam Long eventId) {
        log.info("Пришел POST запрос на создание запроса от пользователя с id = {} для события с id = {}", userId, eventId);
        ParticipationRequestDto request = requestService.createEventRequest(userId, eventId);
        log.info("Ответ отправлен: {}", request);
        return request;
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelEventRequests(@PathVariable Long userId,
                                                       @PathVariable Long requestId) {
        log.info("Пришел PATCH запрос от пользователя с id = {} на отмену запроса с id = {}", userId, requestId);
        ParticipationRequestDto request = requestService.cancelEventRequests(userId, requestId);
        log.info("Ответ отправлен: {}", request);
        return request;
    }
}
