package ru.practicum.main_service.compilation.mapper;

import ru.practicum.main_service.compilation.dto.CompilationDto;
import ru.practicum.main_service.compilation.dto.NewCompilationDto;
import ru.practicum.main_service.compilation.model.Compilation;
import ru.practicum.main_service.event.dto.EventShortDto;
import ru.practicum.main_service.event.model.Event;

import java.util.List;

public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto newCompilationDto, List<Event> events) {
        return Compilation.builder()
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .events(events)
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> eventShortDto) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(eventShortDto)
                .build();
    }
}
