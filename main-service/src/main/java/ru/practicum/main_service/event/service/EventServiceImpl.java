package ru.practicum.main_service.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.category.model.Category;
import ru.practicum.main_service.category.service.CategoryService;
import ru.practicum.main_service.event.dto.*;
import ru.practicum.main_service.event.enums.EventState;
import ru.practicum.main_service.event.enums.EventTypes;
import ru.practicum.main_service.event.mapper.EventMapper;
import ru.practicum.main_service.event.mapper.LocationMapper;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.model.Location;
import ru.practicum.main_service.event.repository.EventRepository;
import ru.practicum.main_service.event.repository.LocationRepository;
import ru.practicum.main_service.exception.DataConflictException;
import ru.practicum.main_service.exception.DataNotFoundException;
import ru.practicum.main_service.exception.DateNotCorrectException;
import ru.practicum.main_service.user.model.User;
import ru.practicum.main_service.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationRepository locationRepository;
    private final EventStatsService statsService;
    private final EventRepository eventRepository;

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            log.error("Некорректо задан временной интервал " +
                    "rangeStart = {}, rangeEnd = {}}", rangeStart, rangeEnd);
            throw new DateNotCorrectException(String.format("Некорректо задан временной интервал " +
                    "rangeStart = %s, rangeEnd = %s", rangeStart, rangeEnd));
        }

        if (rangeStart == null) {
            Optional<Event> event = eventRepository.findById(1L);
            if (event.isEmpty()) {
                return Collections.emptyList();
            } else {
                rangeStart = event.get().getEventDate();
            }
        }

        if (rangeEnd == null) {
            List<Event> events = eventRepository.findAll();
            if (events.isEmpty()) {
                return Collections.emptyList();
            }
            LinkedList<Event> linkedListEvents = new LinkedList<>(events);
            linkedListEvents.sort(Comparator.comparing(Event::getId));
            Event event = linkedListEvents.getLast();
            rangeEnd = event.getEventDate();
        }

        if (categories == null) {
            categories = new ArrayList<>();
            for (Long i = 1L; i <= categoryService.categoriesCount(); i++) {
                categories.add(i);
            }
        }

        if (users == null) {
            users = new ArrayList<>();
            for (Long i = 1L; i <= userService.usersCount(); i++) {
                users.add(i);
            }
        }

        if (states == null) {
            states = new ArrayList<>();
            states.addAll(List.of(EventState.PENDING, EventState.CANCELED, EventState.PUBLISHED));
        }

        List<Event> events = eventRepository.findAllByInitiatorIdInAndCategoryIdInAndEventDateBetween(users, categories,
                rangeStart, rangeEnd, PageRequest.of(from / size, size));
        if (states.size() > 0) {
            List<Event> filteredEvents = new ArrayList<>();
            List<EventState> finalStates = states;
            events.forEach(event -> {
                for (EventState state : finalStates) {
                    if (event.getState().equals(state)) {
                        filteredEvents.add(event);
                    }
                }
            });
            events.clear();
            events.addAll(filteredEvents);
        }
        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(events);

        return events.stream().map((event) -> EventMapper.toEventFullDto(event, confirmedRequests.getOrDefault(event.getId(), 0L),
                views.getOrDefault(event.getId(), 0L))).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto editEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        if (updateEventAdminRequest.getEventDate() != null &&
                updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            log.error("Не верно указано время начала мероприятия {}", updateEventAdminRequest.getEventDate());
            throw new DateNotCorrectException(String.format("Не верно указано время начала мероприятия %s",
                    updateEventAdminRequest.getEventDate()));
        }

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new DataNotFoundException(String.format("Событие с id = %d не найдено", eventId)));

        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(getLocation(updateEventAdminRequest.getLocation()));
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            Integer newLimit = updateEventAdminRequest.getParticipantLimit();
            Long eventLimit = statsService.getConfirmedRequests(List.of(event)).getOrDefault(eventId, 0L);
            if (eventLimit != null && newLimit < eventLimit) {
                log.error("Новый лимит участников = {} не должен быть меньше предыдущего = {}", newLimit, eventLimit);
                throw new DataConflictException(String.format("Новый лимит участников = %d не должен быть меньше предыдущего = %d",
                        newLimit, eventLimit));
            }
            event.setParticipantLimit(newLimit);
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            if (!event.getState().equals(EventState.PENDING)) {
                log.error("Можно опубликовать только те события, которые находятся в ожидании публикации, сейчас " +
                        "публикация находится в статусе - {}", event.getState());
                throw new DataConflictException(String.format("Можно опубликовать только те события, которые находятся " +
                        "в ожидании публикации, сейчас публикация находится в статусе - %s", event.getState()));
            }

            switch (updateEventAdminRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }
        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(categoryService.getCategoryById(updateEventAdminRequest.getCategory()));
        }

        Map<Long, Long> views = statsService.getViews(List.of(event));
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(List.of(event));
        Event savedEvent = eventRepository.save(event);

        return EventMapper.toEventFullDto(savedEvent, confirmedRequests.getOrDefault(event.getId(), 0L),
                views.getOrDefault(event.getId(), 0L));
    }

    @Override
    public List<EventShortDto> getAllEventsByUser(Long userId, Pageable pageable) {
        userService.getUserById(userId);

        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(events);

        return events.stream().map(event -> EventMapper.toEventShortDto(event, confirmedRequests.getOrDefault(event.getId(), 0L),
                views.getOrDefault(event.getId(), 0L))).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto createEventByUser(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate() != null && newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            log.error("Не верно указано время начала мероприятия {}", newEventDto.getEventDate());
            throw new DateNotCorrectException(String.format("Не верно указано время начала мероприятия %s",
                    newEventDto.getEventDate()));
        }

        User eventUser = userService.getUserById(userId);
        Category eventCategory = categoryService.getCategoryById(newEventDto.getCategory());
        Location eventLocation = locationRepository.findByLatAndLon(newEventDto.getLocation().getLat(),
                newEventDto.getLocation().getLon()).orElse(locationRepository.save(LocationMapper.toLocation(newEventDto.getLocation())));

        Event newEvent = EventMapper.toEvent(newEventDto, eventUser, eventCategory, eventLocation, LocalDateTime.now(), EventState.PENDING);

        return getEventFullDto(eventRepository.save(newEvent));
    }

    @Override
    public EventFullDto getEventByUser(Long userId, Long eventId) {
        userService.getUserById(userId);

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new DataNotFoundException(String.format("Событие с id = %d не найдено", eventId)));

        return getEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto editEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        if (updateEventUserRequest.getEventDate() != null &&
                updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            log.error("Не верно указано время начала мероприятия {}",
                    updateEventUserRequest.getEventDate());
            throw new DateNotCorrectException(String.format("Не верно указано время начала мероприятия %s",
                    updateEventUserRequest.getEventDate()));
        }

        userService.getUserById(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new DataNotFoundException(String.format("Событие с id = %d не найдено", eventId)));

        if (event.getState().equals(EventState.PUBLISHED)) {
            log.error("Изменять можно только неопубликованные события");
            throw new DataConflictException("Изменять можно только неопубликованные события");
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(getLocation(updateEventUserRequest.getLocation()));
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }
        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(categoryService.getCategoryById(updateEventUserRequest.getCategory()));
        }

        return getEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getEventsByPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                                 LocalDateTime rangeEnd, Boolean onlyAvailable, EventTypes sort, Integer from,
                                                 Integer size, HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            log.error("Некорректо задан временной интервал rangeStart = {}, rangeEnd = {}", rangeStart, rangeEnd);
            throw new DateNotCorrectException(String.format("Некорректо задан временной интервал " +
                    "rangeStart = %s, rangeEnd = %s", rangeStart, rangeEnd));
        }

        if (rangeStart == null) {
            Optional<Event> event = eventRepository.findById(1L);
            if (event.isEmpty()) {
                return Collections.emptyList();
            } else {
                rangeStart = event.get().getEventDate();
            }
        }

        if (rangeEnd == null) {
            List<Event> events = eventRepository.findAll();
            if (events.isEmpty()) {
                return Collections.emptyList();
            }
            LinkedList<Event> linkedListEvents = new LinkedList<>(events);
            linkedListEvents.sort(Comparator.comparing(Event::getId));
            Event event = linkedListEvents.getLast();
            rangeEnd = event.getEventDate();
        }

        if (categories == null) {
            categories = new ArrayList<>();
            for (Long i = 1L; i <= categoryService.categoriesCount(); i++) {
                categories.add(i);
            }
        }

        List<Event> events = new ArrayList<>();

        if (text != null && paid != null) {
            events = eventRepository.findAllByAnnotationOrDescriptionAndCategoryIdInAndPaidAndEventDateBetween(text,
                    text, categories, paid, rangeStart, rangeEnd, PageRequest.of(from / size, size));
        }
        if (text == null) {
            events = eventRepository.findAllByCategoryIdInAndPaidAndEventDateBetween(categories, paid, rangeStart, rangeEnd,
                    PageRequest.of(from / size, size));
        }
        if (paid == null) {
            events = eventRepository.findAllByAnnotationOrDescriptionAndCategoryIdInAndEventDateBetween(text, text, categories,
                    rangeStart, rangeEnd, PageRequest.of(from / size, size));
        }
        if (text == null && paid == null) {
            events = eventRepository.findAllByCategoryIdInAndEventDateBetween(categories,
                    rangeStart, rangeEnd, PageRequest.of(from / size, size));
        }

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Integer> participantLimits = new HashMap<>();
        events.forEach(event -> participantLimits.put(event.getId(), event.getParticipantLimit()));

        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(events);

        List<EventShortDto> eventShortDto = events.stream()
                .map(event -> EventMapper.toEventShortDto(event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());

        if (onlyAvailable) {
            eventShortDto = eventShortDto.stream()
                    .filter(event -> (participantLimits.get(event.getId()) == 0 ||
                            participantLimits.get(event.getId()) > event.getConfirmedRequests()))
                    .collect(Collectors.toList());
        }
        if (sort != null && sort.equals(EventTypes.VIEWS)) {
            eventShortDto.sort(Comparator.comparing(EventShortDto::getViews));
        } else if (sort != null && sort.equals(EventTypes.EVENT_DATE)) {
            eventShortDto.sort(Comparator.comparing(EventShortDto::getEventDate));
        }

        statsService.add(request);
        return eventShortDto;
    }

    @Override
    public EventFullDto getEventByIdByPublic(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new DataNotFoundException(
                String.format("События с id = %d не найдено", eventId)));

        if (!event.getState().equals(EventState.PUBLISHED)) {
            log.error("Событие с id = {} не опубликовано", eventId);
            throw new DataNotFoundException(String.format("Событие с id = %d не опубликовано", eventId));
        }

        statsService.add(request);
        return getEventFullDto(event);
    }

    @Override
    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new DataNotFoundException(
                String.format("События с id = %d не найдено", eventId)));
    }

    @Override
    public List<Event> getEventsByIds(List<Long> eventsIds) {
        return eventRepository.findAllByIdIn(eventsIds);
    }

    @Override
    public List<EventShortDto> getShortEvents(List<Long> eventsIds) {
        List<Event> events = eventRepository.findAllByIdIn(eventsIds);
        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(events);

        return events.stream().map(event -> EventMapper.toEventShortDto(event, views.getOrDefault(event.getId(), 0L),
                confirmedRequests.getOrDefault(event.getId(), 0L))).collect(Collectors.toList());
    }

    private EventFullDto getEventFullDto(Event event) {
        Map<Long, Long> views = statsService.getViews(List.of(event));
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(List.of(event));

        return EventMapper.toEventFullDto(event, confirmedRequests.getOrDefault(event.getId(), 0L),
                views.getOrDefault(event.getId(), 0L));
    }

    private Location getLocation(LocationDto locationDto) {
        Location location = LocationMapper.toLocation(locationDto);
        return locationRepository.findByLatAndLon(location.getLat(), location.getLon())
                .orElseGet(() -> locationRepository.save(location));
    }
}
