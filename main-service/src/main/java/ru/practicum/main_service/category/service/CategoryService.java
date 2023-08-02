package ru.practicum.main_service.category.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.main_service.category.dto.CategoryRequestDto;
import ru.practicum.main_service.category.dto.CategoryResponseDto;
import ru.practicum.main_service.category.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryResponseDto add(CategoryRequestDto categoryRequestDto);

    List<CategoryResponseDto> getAll(Pageable pageable);

    CategoryResponseDto getById(Long id);

    CategoryResponseDto edit(Long id, CategoryRequestDto categoryRequestDto);

    void deleteById(Long id);

    Category getCategoryById(Long id);
}
