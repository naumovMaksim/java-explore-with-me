package ru.practicum.main_service.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.main_service.event.dto.*;
import ru.practicum.main_service.event.enums.EventState;
import ru.practicum.main_service.event.enums.EventTypes;
import ru.practicum.main_service.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventFullDto> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto editEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getAllEventsByUser(Long userId, Pageable pageable);

    EventFullDto createEventByUser(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByUser(Long userId, Long eventId);

    EventFullDto editEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventShortDto> getEventsByPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd, Boolean onlyAvailable, EventTypes sort, Integer from,
                                          Integer size, HttpServletRequest request);

    EventFullDto getEventByIdByPublic(Long eventId, HttpServletRequest request);

    Event getEventById(Long eventId);

    List<Event> getEventsByIds(List<Long> eventsIds);

    List<EventShortDto> getShortEvents(List<Long> eventsIds);
}
