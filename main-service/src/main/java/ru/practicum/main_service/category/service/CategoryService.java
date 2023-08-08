package ru.practicum.main_service.category.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.main_service.category.dto.NewCategoryDto;
import ru.practicum.main_service.category.dto.CategoryDto;
import ru.practicum.main_service.category.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto add(NewCategoryDto newCategoryDto);

    List<CategoryDto> getAll(Pageable pageable);

    List<Category> getAll();

    CategoryDto getById(Long id);

    CategoryDto edit(Long id, NewCategoryDto newCategoryDto);

    void deleteById(Long id);

    Category getCategoryById(Long id);
}
