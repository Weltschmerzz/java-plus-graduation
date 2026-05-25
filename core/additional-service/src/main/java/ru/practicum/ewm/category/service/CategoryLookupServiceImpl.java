package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.api.CategoryLookupService;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.events.dto.CategoryDto;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryLookupServiceImpl implements CategoryLookupService {

    private final CategoryRepository categoryRepository;

    @Override
    public void requireExists(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("Категория с id=" + categoryId + " не найдена!");
        }
    }

    @Override
    public CategoryDto getById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория с id=" + categoryId + " не найдена!"));
        return toDto(category);
    }

    @Override
    public Map<Long, CategoryDto> getByIds(Collection<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, CategoryDto> result = new HashMap<>();
        for (Category category : categoryRepository.findAllById(categoryIds)) {
            result.put(category.getId(), toDto(category));
        }
        return result;
    }

    private static CategoryDto toDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }
}
