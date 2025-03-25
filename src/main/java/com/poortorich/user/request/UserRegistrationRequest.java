package com.poortorich.user.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.poortorich.user.constants.UserResponseMessages;
import com.poortorich.user.constants.UserValidationRules;
import com.poortorich.user.entity.enums.Gender;
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

    @NotBlank(message = UserResponseMessages.NAME_REQUIRED)
    @Size(max = UserValidationRules.NAME_MAX_LENGTH, message = UserResponseMessages.NAME_TOO_LONG)
    @Pattern(regexp = UserValidationRules.NO_BLANK_REGEX, message = UserResponseMessages.NAME_CONTAINS_BLANK)
    private String name;

    @NotBlank(message = UserResponseMessages.NICKNAME_REQUIRED)
    @Size(max = UserValidationRules.NICKNAME_MAX_LENGTH, message = UserResponseMessages.NICKNAME_TOO_LONG)
    @Pattern(regexp = UserValidationRules.NO_BLANK_REGEX, message = UserResponseMessages.NICKNAME_CONTAINS_BLANK)
    @Pattern(regexp = UserValidationRules.NICKNAME_START_REGEX, message = UserResponseMessages.NICKNAME_INVALID_START_CHAR)
    @Pattern(
            regexp = UserValidationRules.NICKNAME_ALLOWED_CHARS_REGEX,
            message = UserResponseMessages.NICKNAME_CONTAINS_SPECIAL_CHAR
    )
    private String nickname;

    @NotBlank(message = UserResponseMessages.USERNAME_REQUIRED)
    @Size(
            max = UserValidationRules.USERNAME_MAX_LENGTH,
            min = UserValidationRules.USERNAME_MIN_LENGTH,
            message = UserResponseMessages.USERNAME_LENGTH_INVALID
    )
    @Pattern(regexp = UserValidationRules.NO_BLANK_REGEX, message = UserResponseMessages.USERNAME_CONTAINS_BLANK)
    @Pattern(
            regexp = UserValidationRules.USERNAME_ALLOWED_CHARS_REGEX,
            message = UserResponseMessages.USERNAME_CONTAINS_INVALID_CHAR
    )
    private String username;

    @NotBlank(message = UserResponseMessages.PASSWORD_REQUIRED)
    @Size(
            max = UserValidationRules.PASSWORD_MAX_LENGTH,
            min = UserValidationRules.PASSWORD_MIN_LENGTH,
            message = UserResponseMessages.PASSWORD_LENGTH_INVALID
    )
    @Pattern(
            regexp = UserValidationRules.NO_BLANK_REGEX,
            message = UserResponseMessages.PASSWORD_CONTAINS_BLANK
    )
    @Pattern(
            regexp = UserValidationRules.PASSWORD_NO_KOREAN_REGEX,
            message = UserResponseMessages.PASSWORD_CONTAINS_KOREAN
    )
    @Pattern(
            regexp = UserValidationRules.PASSWORD_REGEX,
            message = UserResponseMessages.PASSWORD_INVALID
    )
    private String password;

    @NotBlank(message = UserResponseMessages.PASSWORD_CONFIRM_REQUIRED)
    private String UserValidationConstraints;

    @NotBlank(message = UserResponseMessages.BIRTHDAY_REQUIRED)
    @Pattern(regexp = UserValidationRules.BIRTHDAY_FORMAT_REGEX, message = UserResponseMessages.BIRTHDAY_INVALID)
    @JsonFormat(shape = Shape.STRING, pattern = UserValidationRules.BIRTHDAY_PATTERN)
    private LocalDate birth;

    @NotBlank(message = UserResponseMessages.EMAIL_REQUIRED)
    @Email(message = UserResponseMessages.EMAIL_INVALID)
    private String email;

    @NotBlank(message = UserResponseMessages.GENDER_REQUIRED)
    private Gender gender;

    private String job;
}
