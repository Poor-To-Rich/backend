package com.poortorich.user.request;

import com.poortorich.user.constants.UserResponseMessages;
import com.poortorich.user.constants.UserValidationRules;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UsernameCheckRequest {

    @NotBlank(message = UserResponseMessages.USERNAME_REQUIRED)
    @Size(
            max = UserValidationRules.USERNAME_MAX_LENGTH,
            min = UserValidationRules.USERNAME_MIN_LENGTH,
            message = UserResponseMessages.USERNAME_LENGTH_INVALID
    )
    @Pattern(regexp = UserValidationRules.NO_BLANK_PATTERN, message = UserResponseMessages.USERNAME_CONTAINS_BLANK)
    @Pattern(
            regexp = UserValidationRules.USERNAME_ALLOWED_CHARS_PATTERN,
            message = UserResponseMessages.USERNAME_CONTAINS_INVALID_CHAR
    )
    private String username;
}
