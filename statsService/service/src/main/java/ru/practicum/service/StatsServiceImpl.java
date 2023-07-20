package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;

    @Override
    @Transactional
    public void add(EndpointHitDto endpointHitDto) {

        repository.save(StatsMapper.toStats(endpointHitDto));
    }

    @Override
    public List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            log.error("Не верный временной промежуток");
            throw new IllegalArgumentException("Не верный временной промежуток");
        }
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return repository.getAllWithUniq(start, end);
            } else {
                return repository.getAll(start, end);
            }
        } else {
            if (unique) {
                return repository.getAllWithUrisAndUniq(start, end, uris);
            } else {
                return repository.getAllWithUri(start, end, uris);
            }
        }
    }

    private Long getHitsCount(LocalDateTime start, LocalDateTime end, String ip) {
        return repository.getStatsCount(start, end, ip);
    }
}
