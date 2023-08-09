package ru.practicum.main_service.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.category.dto.CategoryDto;
import ru.practicum.main_service.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Slf4j
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getAll(@RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                    @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("Пришел GET запрос на получение всех категорий с параметрами from = {}, size = {}", from, size);
        List<CategoryDto> categories = categoryService.getAll(PageRequest.of(from / size, size));
        log.info("Ответ отправлен {}", categories);
        return categories;
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getById(@PathVariable Long catId) {
        log.info("Пришел GET запрос на получение категории с id = {}", catId);
        CategoryDto category = categoryService.getById(catId);
        log.info("Ответ отправлен {}", category);
        return category;
    }
}
