package ru.practicum.main_service.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.compilation.dto.CompilationDto;
import ru.practicum.main_service.compilation.dto.NewCompilationDto;
import ru.practicum.main_service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main_service.compilation.mapper.CompilationMapper;
import ru.practicum.main_service.compilation.model.Compilation;
import ru.practicum.main_service.compilation.repository.CompilationRepository;
import ru.practicum.main_service.event.dto.EventShortDto;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.service.EventService;
import ru.practicum.main_service.exception.DataNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final EventService eventService;
    private final CompilationRepository compilationRepository;

    @Override
    @Transactional
    public CompilationDto add(NewCompilationDto newCompilationDto) {
        List<Event> events = new ArrayList<>();

        if (!newCompilationDto.getEvents().isEmpty()) {
            events = eventService.getEventsByIds(newCompilationDto.getEvents());
            if (events.size() != newCompilationDto.getEvents().size()) {
                log.error("Не все события найдены");
                throw new DataNotFoundException("Не все события найдены");
            }
        }

        Compilation compilation = compilationRepository.save(CompilationMapper.toCompilation(newCompilationDto, events));

        return CompilationMapper.toCompilationDto(compilation, eventService.getShortEvents(newCompilationDto.getEvents()));
    }

    @Override
    @Transactional
    public CompilationDto edit(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new DataNotFoundException(String.format("Подборка с id = %d не найдена", compId)));

        if (updateCompilationRequest.getEvents() != null) {
            List<Event> events = eventService.getEventsByIds(updateCompilationRequest.getEvents());
            if (events.size() != updateCompilationRequest.getEvents().size()) {
                log.error("Не все события найдены");
                throw new DataNotFoundException("Не все события найдены");
            }
            compilation.setEvents(events);
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        compilationRepository.save(compilation);

        return CompilationMapper.toCompilationDto(compilation, eventService.getShortEvents(updateCompilationRequest.getEvents()));
    }

    @Override
    @Transactional
    public void deleteById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new DataNotFoundException(String.format("Подборка с id = %d не найдена", compId)));
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, Pageable pageable) {
        List<Compilation> compilations;

        if (pinned == null) {
            compilations = compilationRepository.findAll(pageable).toList();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, pageable);
        }

        List<CompilationDto> result = new ArrayList<>();

        compilations.forEach(compilation -> {
            List<Long> eventIds = new ArrayList<>();
            compilation.getEvents().forEach(event -> eventIds.add(event.getId()));
            result.add(CompilationMapper.toCompilationDto(compilation, eventService.getShortEvents(eventIds)));
        });

        return result;
    }

    @Override
    public CompilationDto getById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new DataNotFoundException(String.format("Подборка с id = %d не найдена", compId)));

        List<Long> eventIds = new ArrayList<>();
        compilation.getEvents().forEach(event -> eventIds.add(event.getId()));
        List<EventShortDto> eventShortDto = eventService.getShortEvents(eventIds);

        return CompilationMapper.toCompilationDto(compilation, eventShortDto);
    }
}
