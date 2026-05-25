package ru.practicum.ewm.category.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.ewm.events.dto.CategoryDto;

import java.util.Collection;
import java.util.Map;

@FeignClient(name = "additional-service", path = "/internal/categories")
public interface CategoryServiceClient {

    @GetMapping("/{categoryId}/exists")
    void requireExists(@PathVariable Long categoryId);

    @GetMapping("/{categoryId}")
    CategoryDto getById(@PathVariable Long categoryId);

    @PostMapping
    Map<Long, CategoryDto> getByIds(@RequestBody Collection<Long> categoryIds);
}
