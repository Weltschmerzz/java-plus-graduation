package ru.practicum.ewm.category.api;

import ru.practicum.ewm.events.dto.CategoryDto;

import java.util.Collection;
import java.util.Map;

public interface CategoryLookupService {

    void requireExists(Long categoryId);

    CategoryDto getById(Long categoryId);

    Map<Long, CategoryDto> getByIds(Collection<Long> categoryIds);
}
