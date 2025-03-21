package com.poortorich.user.request;

import com.poortorich.user.constants.UserConstant.ValidationConstraints;
import com.poortorich.user.constants.UserConstant.ValidationMessages;
import com.poortorich.user.entity.enums.Gender;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {

    @NotBlank(message = ValidationMessages.NAME_REQUIRED)
    @Size(max = ValidationConstraints.NAME_MAX_LENGTH, message = ValidationMessages.NAME_TOO_LONG)
    @Pattern(regexp = ValidationConstraints.NO_BLANK_REGEX, message = ValidationMessages.NAME_CONTAINS_BLANK)
    private String name;

    @NotBlank(message = ValidationMessages.NICKNAME_REQUIRED)
    @Size(max = ValidationConstraints.NICKNAME_MAX_LENGTH, message = ValidationMessages.NICKNAME_TOO_LONG)
    @Pattern(regexp = ValidationConstraints.NO_BLANK_REGEX, message = ValidationMessages.NICKNAME_CONTAINS_BLANK)
    @Pattern(regexp = ValidationConstraints.NICKNAME_START_REGEX, message = ValidationMessages.NICKNAME_INVALID_START_CHAR)
    @Pattern(
            regexp = ValidationConstraints.NICKNAME_ALLOWED_CHARS_REGEX,
            message = ValidationMessages.NICKNAME_CONTAINS_SPECIAL_CHAR
    )
    private String nickname;

    @NotBlank(message = ValidationMessages.USERNAME_REQUIRED)
    @Size(
            max = ValidationConstraints.USERNAME_MAX_LENGTH,
            min = ValidationConstraints.USERNAME_MIN_LENGTH,
            message = ValidationMessages.USERNAME_LENGTH_INVALID
    )
    @Pattern(regexp = ValidationConstraints.NO_BLANK_REGEX, message = ValidationMessages.USERNAME_CONTAINS_BLANK)
    @Pattern(
            regexp = ValidationConstraints.USERNAME_ALLOWED_CHARS_REGEX,
            message = ValidationMessages.USERNAME_CONTAINS_INVALID_CHAR
    )
    private String username;

    @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
    @Size(
            max = ValidationConstraints.PASSWORD_MAX_LENGTH,
            min = ValidationConstraints.PASSWORD_MIN_LENGTH,
            message = ValidationMessages.PASSWORD_LENGTH_INVALID
    )
    @Pattern(
            regexp = ValidationConstraints.NO_BLANK_REGEX,
            message = ValidationMessages.PASSWORD_CONTAINS_BLANK
    )
    @Pattern(
            regexp = ValidationConstraints.PASSWORD_NO_KOREAN_REGEX,
            message = ValidationMessages.PASSWORD_CONTAINS_KOREAN
    )
    @Pattern(
            regexp = ValidationConstraints.PASSWORD_REGEX,
            message = ValidationMessages.PASSWORD_INVALID
    )
    private String password;

    @NotBlank(message = ValidationMessages.PASSWORD_CONFIRM_REQUIRED)
    private String passwordConfirm;

    @NotBlank(message = ValidationMessages.BIRTHDAY_REQUIRED)
    @Pattern(regexp = ValidationConstraints.BIRTHDAY_FORMAT_REGEX, message = ValidationMessages.BIRTHDAY_INVALID)
    private LocalDate birth;

    @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
    @Email(message = ValidationMessages.EMAIL_INVALID)
    private String email;

    @NotBlank(message = ValidationMessages.GENDER_REQUIRED)
    private Gender gender;

    @Nullable
    private String job;
}
