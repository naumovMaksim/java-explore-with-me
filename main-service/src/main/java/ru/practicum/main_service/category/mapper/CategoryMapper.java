package ru.practicum.main_service.category.mapper;

import ru.practicum.main_service.category.dto.NewCategoryDto;
import ru.practicum.main_service.category.dto.CategoryDto;
import ru.practicum.main_service.category.model.Category;

public class CategoryMapper {
    public static Category toCategory(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }

    public static CategoryDto toCategoryResponseDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
