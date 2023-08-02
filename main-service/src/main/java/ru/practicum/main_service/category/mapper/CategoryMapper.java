package ru.practicum.main_service.category.mapper;

import ru.practicum.main_service.category.dto.CategoryRequestDto;
import ru.practicum.main_service.category.dto.CategoryResponseDto;
import ru.practicum.main_service.category.model.Category;

public class CategoryMapper {
    public static Category toCategory(CategoryRequestDto categoryRequestDto) {
        return Category.builder()
                .name(categoryRequestDto.getName())
                .build();
    }

    public static Category toCategory(CategoryResponseDto categoryResponseDto) {
        return Category.builder()
                .name(categoryResponseDto.getName())
                .build();
    }

    public static CategoryResponseDto toCategoryResponseDto(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
