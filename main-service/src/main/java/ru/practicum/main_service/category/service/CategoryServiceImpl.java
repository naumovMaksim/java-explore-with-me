package ru.practicum.main_service.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.category.dto.CategoryRequestDto;
import ru.practicum.main_service.category.dto.CategoryResponseDto;
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
    public CategoryResponseDto add(CategoryRequestDto categoryRequestDto) {
        Category category = repository.save(CategoryMapper.toCategory(categoryRequestDto));
        return CategoryMapper.toCategoryResponseDto(category);
    }

    @Override
    public List<CategoryResponseDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).stream().map(CategoryMapper::toCategoryResponseDto).collect(Collectors.toList());
    }

    @Override
    public CategoryResponseDto getById(Long id) {
        Category category = repository.findById(id).orElseThrow(() ->
                new DataNotFoundException(String.format("Категория с id %d не найдена", id)));
        return CategoryMapper.toCategoryResponseDto(category);
    }

    @Override
    @Transactional
    public CategoryResponseDto edit(Long id, CategoryRequestDto categoryRequestDto) {
        Category category = repository.findById(id).orElseThrow(() ->
                new DataNotFoundException(String.format("Категория с id %d не найдена", id)));
        Category editCategory = repository.save(CategoryMapper.toCategory(categoryRequestDto));
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
}
