package ru.practicum.main_service.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main_service.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.main_service.event.dto.ParticipationRequestDto;
import ru.practicum.main_service.event.enums.EventState;
import ru.practicum.main_service.event.enums.RequestStatus;
import ru.practicum.main_service.event.mapper.RequestMapper;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.model.Request;
import ru.practicum.main_service.event.repository.RequestRepository;
import ru.practicum.main_service.exception.DataConflictException;
import ru.practicum.main_service.exception.DataNotFoundException;
import ru.practicum.main_service.user.model.User;
import ru.practicum.main_service.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final UserService userService;
    private final EventService eventService;
    private final EventStatsService statsService;
    private final RequestRepository repository;

    @Override
    public List<ParticipationRequestDto> getEventRequestsByRequester(Long userId) {
        userService.getUserById(userId);
        List<Request> requests = repository.findAllByRequesterId(userId);

        return requests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto createEventRequest(Long userId, Long eventId) {
        User user = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);

        if (Objects.equals(event.getInitiator().getId(), userId)) {
            log.error("Нельзя содавать запрос на собственное событие");
            throw new DataConflictException("Нельзя содавать запрос на собственное событие");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            log.error("Нельзя создать запрос на не опубликованное событие");
            throw new DataConflictException("Нельзя создать запрос на не опубликованное событие");
        }

        Optional<Request> lastRequest = repository.findByEventIdAndRequesterId(eventId, userId);

        if (lastRequest.isPresent()) {
            log.error("Нельзя создавать повторный запрос");
            throw new DataConflictException("Нельзя создавать повторный запрос");
        }

        if (event.getParticipantLimit() != 0 &&
                (statsService.getConfirmedRequests(List.of(event)).getOrDefault(eventId, 0L) + 1) > event.getParticipantLimit()) {
            log.error("Лимит подтвержденных запросов на участие достигнут = {}", event.getParticipantLimit());
            throw new DataConflictException(String.format("Лимит подтвержденных запросов на участие достигнут = %d", event.getParticipantLimit()));
        }

        Request newRequest = Request.builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            newRequest.setStatus(RequestStatus.CONFIRMED);
        } else {
            newRequest.setStatus(RequestStatus.PENDING);
        }

        return RequestMapper.toParticipationRequestDto(repository.save(newRequest));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelEventRequests(Long userId, Long requestId) {
        userService.getUserById(userId);

        Request request = repository.findById(requestId).orElseThrow(() -> new DataNotFoundException(
                String.format("Заявка с id = %d не найдена", requestId)));

        if (!Objects.equals(request.getRequester().getId(), userId)) {
            log.error("Пользователь с id = {} не явлется владельцем", userId);
            throw new DataConflictException(String.format("Пользователь с id = %d не явлется владельцем", userId));
        }

        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.toParticipationRequestDto(repository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getEventRequestsByEventOwner(Long userId, Long eventId) {
        userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            log.error("Пользователь с id = {} не явлется владельцем", userId);
            throw new DataConflictException(String.format("Пользователь с id = %d не явлется владельцем", userId));
        }

        List<Request> requests = repository.findAllByEventId(eventId);

        return requests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult editEventRequestsByEventOwner(Long userId, Long eventId,
                                                                        EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            log.error("Пользователь с id = {} не явлется владельцем", userId);
            throw new DataConflictException(String.format("Пользователь с id = %d не явлется владельцем", userId));
        }

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0 || eventRequestStatusUpdateRequest.getRequestIds().isEmpty()) {
            return new EventRequestStatusUpdateResult(Collections.emptyList(), Collections.emptyList());
        }

        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();
        List<Request> requests = repository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

        if (requests.size() != eventRequestStatusUpdateRequest.getRequestIds().size()) {
            log.error("Не все запросы на участие найдены");
            throw new DataNotFoundException("Не все запросы на участие найдены");
        }

        if (!requests.stream().map(Request::getStatus).allMatch(RequestStatus.PENDING::equals)) {
            log.error("Изменять можно только заявки, находящиеся в ожидании");
            throw new DataConflictException("Изменять можно только заявки, находящиеся в ожидании");
        }

        if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.REJECTED)) {
            requests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
            rejected.addAll(repository.saveAll(requests));
        } else {
            long confirmedRequest = statsService.getConfirmedRequests(List.of(event)).getOrDefault(eventId, 0L) +
                    eventRequestStatusUpdateRequest.getRequestIds().size();
            if (event.getParticipantLimit() != 0 && (confirmedRequest > event.getParticipantLimit())) {
                log.error("Лимит подтвержденных запросов на участие достигнут = {}", event.getParticipantLimit());
                throw new DataConflictException(String.format("Лимит подтвержденных запросов на участие достигнут = %d", event.getParticipantLimit()));
            }

            requests.forEach(request -> request.setStatus(RequestStatus.CONFIRMED));
            confirmed.addAll(repository.saveAll(requests));

            if (confirmedRequest >= event.getParticipantLimit()) {
                List<Request> pendingRequests = repository.findAllByEventIdAndStatus(eventId, RequestStatus.PENDING);
                pendingRequests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
                rejected.addAll(repository.saveAll(pendingRequests));
            }
        }

        return new EventRequestStatusUpdateResult(confirmed.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList()),
                rejected.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList()));
    }
}
