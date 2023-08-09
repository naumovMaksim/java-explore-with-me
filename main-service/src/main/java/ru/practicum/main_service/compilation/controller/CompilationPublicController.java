package ru.practicum.main_service.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.compilation.dto.CompilationDto;
import ru.practicum.main_service.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
@Slf4j
public class CompilationPublicController {
    private final CompilationService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getAll(@RequestParam(required = false) Boolean pinned,
                                       @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                       @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("Пришел GET запрос на получение всех Compilations с параметрами pinned = {}, from = {}, size = {}", pinned,
                from, size);
        List<CompilationDto> compilationDto = service.getAll(pinned, PageRequest.of(from / size, size));
        log.info("Ответ отправлен {}", compilationDto);
        return compilationDto;
    }

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getById(@PathVariable Long compId) {
        log.info("Пришел GET запрос на получение Compilation с id = {}", compId);
        CompilationDto compilationDto = service.getById(compId);
        log.info("Ответ отправлен {}", compilationDto);
        return compilationDto;
    }
}
