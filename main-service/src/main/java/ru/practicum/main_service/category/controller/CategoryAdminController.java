package ru.practicum.main_service.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.category.dto.NewCategoryDto;
import ru.practicum.main_service.category.dto.CategoryDto;
import ru.practicum.main_service.category.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
@Slf4j
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto add(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Пришел POST запрос на создание новой категроии {}", newCategoryDto);
        CategoryDto category = categoryService.add(newCategoryDto);
        log.info("Ответ отправлен {}", category);
        return category;
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto edit(@PathVariable Long catId, @Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Пришел PATCH запрос на изменение данных на {} в категории с id = {}", newCategoryDto, catId);
        CategoryDto category = categoryService.edit(catId, newCategoryDto);
        log.info("Ответ отправлен {}", category);
        return category;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long catId) {
        log.info("Пришел DELETE запрос на удаление категории с id = {}", catId);
        categoryService.deleteById(catId);
        log.info("Категория с id = {} удалена", catId);
    }
}
