package com.poortorich.category.validator;

import com.poortorich.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryValidator {

    private final CategoryRepository categoryRepository;

    public Boolean isNameUsed(String name) {
        return categoryRepository.findAll()
                .stream()
                .anyMatch(category -> category.getName().equals(name));
    }

    public Boolean isNameUsed(String name, Long id) {
        return categoryRepository.findAll()
                .stream()
                .anyMatch(category -> category.getName().equals(name) && !category.getId().equals(id));
    }
}
