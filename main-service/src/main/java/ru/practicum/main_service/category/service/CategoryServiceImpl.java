package ru.practicum.main_service.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.category.dto.NewCategoryDto;
import ru.practicum.main_service.category.dto.CategoryDto;
import ru.practicum.main_service.category.mapper.CategoryMapper;
import ru.practicum.main_service.category.model.Category;
import ru.practicum.main_service.category.repository.CategoryRepository;
import ru.practicum.main_service.exception.DataNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;

    @Override
    @Transactional
    public CategoryDto add(NewCategoryDto newCategoryDto) {
        Category category = repository.save(CategoryMapper.toCategory(newCategoryDto));
        return CategoryMapper.toCategoryResponseDto(category);
    }

    @Override
    public List<CategoryDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).stream().map(CategoryMapper::toCategoryResponseDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(Long id) {
        Category category = repository.findById(id).orElseThrow(() ->
                new DataNotFoundException(String.format("Категория с id %d не найдена", id)));
        return CategoryMapper.toCategoryResponseDto(category);
    }

    @Override
    @Transactional
    public CategoryDto edit(Long id, NewCategoryDto newCategoryDto) {
        Category category = repository.findById(id).orElseThrow(() ->
                new DataNotFoundException(String.format("Категория с id %d не найдена", id)));
        if (newCategoryDto.getName() != null) {
            category.setName(newCategoryDto.getName());
        }
        Category editCategory = repository.save(category);
        return CategoryMapper.toCategoryResponseDto(editCategory);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Category category = repository.findById(id).orElseThrow(() ->
                new DataNotFoundException(String.format("Категория с id %d не найдена", id)));
        repository.deleteById(category.getId());
    }

    @Override
    public Category getCategoryById(Long id) {
        return repository.findById(id).orElseThrow(() -> new DataNotFoundException("Категория не найдена"));
    }

    @Override
    public Long categoriesCount() {
        return (long) repository.findAll().size();
    }
}
