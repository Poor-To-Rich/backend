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
public class NicknameCheckRequest {
    
    @NotBlank(message = UserResponseMessages.NICKNAME_REQUIRED)
    @Size(max = UserValidationRules.NICKNAME_MAX_LENGTH, message = UserResponseMessages.NICKNAME_TOO_LONG)
    @Pattern(regexp = UserValidationRules.NO_BLANK_PATTERN, message = UserResponseMessages.NICKNAME_CONTAINS_BLANK)
    @Pattern(regexp = UserValidationRules.NICKNAME_START_PATTERN, message = UserResponseMessages.NICKNAME_INVALID_START_CHAR)
    @Pattern(
            regexp = UserValidationRules.NICKNAME_ALLOWED_CHARS_PATTERN,
            message = UserResponseMessages.NICKNAME_CONTAINS_SPECIAL_CHAR
    )
    private String nickname;
}
