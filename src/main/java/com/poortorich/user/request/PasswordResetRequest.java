package com.poortorich.user.request;

import com.poortorich.user.constants.UserResponseMessages;
import com.poortorich.user.constants.UserValidationRules;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordResetRequest {

    @NotBlank(message = UserResponseMessages.PASSWORD_REQUIRED)
    @Size(
            max = UserValidationRules.PASSWORD_MAX_LENGTH,
            min = UserValidationRules.PASSWORD_MIN_LENGTH,
            message = UserResponseMessages.PASSWORD_LENGTH_INVALID
    )
    @Pattern(
            regexp = UserValidationRules.NO_BLANK_PATTERN,
            message = UserResponseMessages.PASSWORD_CONTAINS_BLANK
    )
    @Pattern(
            regexp = UserValidationRules.PASSWORD_NO_KOREAN_PATTERN,
            message = UserResponseMessages.PASSWORD_CONTAINS_KOREAN
    )
    @Pattern(
            regexp = UserValidationRules.PASSWORD_PATTERN,
            message = UserResponseMessages.PASSWORD_INVALID
    )
    private String newPassword;

    @NotBlank(message = UserResponseMessages.PASSWORD_CONFIRM_REQUIRED)
    private String confirmNewPassword;

    @Email
    @NotBlank(message = UserResponseMessages.EMAIL_REQUIRED)
    private String email;
}
