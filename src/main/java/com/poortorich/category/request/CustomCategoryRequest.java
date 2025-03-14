package com.poortorich.category.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomCategoryRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String color;
}
