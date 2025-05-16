package com.poortorich.category.request;

import com.poortorich.category.constants.CategoryResponseMessage;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryVisibilityRequest {

    @NotNull(message = CategoryResponseMessage.CATEGORY_VISIBILITY_REQUIRED)
    private Boolean visibility;
}
