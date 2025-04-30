package com.poortorich.category.request;

import com.poortorich.category.constants.CategoryResponseMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryInfoRequest {

    @NotBlank(message = CategoryResponseMessage.CATEGORY_NAME_REQUIRED)
    private String name;

    @NotBlank(message = CategoryResponseMessage.CATEGORY_COLOR_REQUIRED)
    private String color;
}
