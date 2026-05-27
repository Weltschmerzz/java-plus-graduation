package ru.practicum.ewm.events.client;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.api.CategoryLookupService;
import ru.practicum.ewm.events.dto.CategoryDto;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Service
@Primary
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ewm.event-service.categories.lookup.mode", havingValue = "feign")
public class FeignCategoryLookupService implements CategoryLookupService {

    private final CategoryServiceClient categoryServiceClient;

    @Override
    public void requireExists(Long categoryId) {
        categoryServiceClient.requireExists(categoryId);
    }

    @Override
    public CategoryDto getById(Long categoryId) {
        return categoryServiceClient.getById(categoryId);
    }

    @Override
    public Map<Long, CategoryDto> getByIds(Collection<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return categoryServiceClient.getByIds(categoryIds);
    }
}
