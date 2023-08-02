package ru.practicum.main_service.event.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.repository.RequestRepository;
import ru.practicum.stats_service.model.ViewStats;
import ru.practicum.stats_client.StatsClient;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventStatsServiceImpl implements EventStatsService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsClient statsClient;
    private final RequestRepository requestRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value(value = "${app.name}")
    private String appName;

    @Override
    public void add(HttpServletRequest request) {
        statsClient.add(appName, request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter));
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        ResponseEntity<Object> response = statsClient.getStats(start, end, uris, unique);

        try {
            return Arrays.asList(mapper.readValue(mapper.writeValueAsString(response.getBody()), ViewStats[].class));
        } catch (IOException exception) {
            throw new ClassCastException(exception.getMessage());
        }
    }

    @Override
    public Map<Long, Long> getViews(List<Event> events) {
        Map<Long, Long> views = new HashMap<>();

        if (events.isEmpty()) {
            return views;
        }

        List<Event> published = events.stream().filter(event -> event.getPublishedOn() != null).collect(Collectors.toList());

        Optional<LocalDateTime> timeOfPublish = published.stream().map(Event::getPublishedOn).filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        if (timeOfPublish.isPresent()) {
            LocalDateTime start = timeOfPublish.get();
            LocalDateTime end = LocalDateTime.now();
            List<String> uris = published.stream().map(Event::getId).map(id -> ("/events/" + id)).collect(Collectors.toList());

            List<ViewStats> stats = getStats(start, end, uris, null);
            stats.forEach(stat -> {
                Long eventId = Long.parseLong(stat.getUri().split("/", 0)[2]);
                views.put(eventId, views.getOrDefault(eventId, 0L) + stat.getHits());
            });
        }
        return views;
    }

    @Override
    public Map<Long, Long> getConfirmedRequests(List<Event> events) {
        List<Event> checkEvents = events.stream().filter(event -> event.getPublishedOn() != null).collect(Collectors.toList());
        List<Long> eventIds = checkEvents.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> requests = new HashMap<>();

        if (!eventIds.isEmpty()) {
            requestRepository.getConfirmedRequests(eventIds).forEach(stat -> requests.put(stat.getEventId(), stat.getConfirmedRequests()));
        }

        return requests;
    }
}
