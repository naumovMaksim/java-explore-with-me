package ru.practicum.main_service.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.compilation.dto.CompilationDto;
import ru.practicum.main_service.compilation.dto.NewCompilationDto;
import ru.practicum.main_service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main_service.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@Slf4j
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto add(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Пришел POST запрос на добавление Compilation {}", newCompilationDto);
        CompilationDto compilationDto = compilationService.add(newCompilationDto);
        log.info("Ответ отправлен {}", compilationDto);
        return compilationDto;
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto edit(@PathVariable Long compId,
                               @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.info("Пришел PATCH запрос на измнение данных Compilation с Id = {} на данные: {}", compId, updateCompilationRequest);
        CompilationDto compilationDto = compilationService.edit(compId, updateCompilationRequest);
        log.info("Ответ отправлен {}", compilationDto);
        return compilationDto;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long compId) {
        log.info("Пришел запрос на удаление Compilation с Id = {}", compId);
        compilationService.deleteById(compId);
        log.info("Compilation c id = {} удален", compId);
    }
}
