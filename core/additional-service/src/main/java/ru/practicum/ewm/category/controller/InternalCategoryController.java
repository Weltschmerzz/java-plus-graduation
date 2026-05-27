package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.category.api.CategoryLookupService;
import ru.practicum.ewm.events.dto.CategoryDto;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/internal/categories")
@RequiredArgsConstructor
public class InternalCategoryController {

    private final CategoryLookupService categoryLookupService;

    @GetMapping("/{categoryId}/exists")
    public ResponseEntity<Void> requireExists(@PathVariable Long categoryId) {
        categoryLookupService.requireExists(categoryId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> getById(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryLookupService.getById(categoryId));
    }

    @PostMapping
    public ResponseEntity<Map<Long, CategoryDto>> getByIds(@RequestBody Collection<Long> categoryIds) {
        return ResponseEntity.ok(categoryLookupService.getByIds(categoryIds));
    }
}
