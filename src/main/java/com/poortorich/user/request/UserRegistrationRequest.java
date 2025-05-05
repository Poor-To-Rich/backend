package com.poortorich.user.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.poortorich.user.constants.UserResponseMessages;
import com.poortorich.user.constants.UserValidationRules;
import com.poortorich.user.entity.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequest {

    @NotBlank(message = UserResponseMessages.NAME_REQUIRED)
    @Size(max = UserValidationRules.NAME_MAX_LENGTH, message = UserResponseMessages.NAME_TOO_LONG)
    @Pattern(regexp = UserValidationRules.NO_BLANK_PATTERN, message = UserResponseMessages.NAME_CONTAINS_BLANK)
    private String name;

    @NotBlank(message = UserResponseMessages.NICKNAME_REQUIRED)
    @Size(max = UserValidationRules.NICKNAME_MAX_LENGTH, message = UserResponseMessages.NICKNAME_TOO_LONG)
    @Pattern(regexp = UserValidationRules.NO_BLANK_PATTERN, message = UserResponseMessages.NICKNAME_CONTAINS_BLANK)
    @Pattern(regexp = UserValidationRules.NICKNAME_START_PATTERN, message = UserResponseMessages.NICKNAME_INVALID_START_CHAR)
    @Pattern(
            regexp = UserValidationRules.NICKNAME_ALLOWED_CHARS_PATTERN,
            message = UserResponseMessages.NICKNAME_CONTAINS_SPECIAL_CHAR
    )
    private String nickname;

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
    private String password;

    @NotBlank(message = UserResponseMessages.PASSWORD_CONFIRM_REQUIRED)
    private String passwordConfirm;

    @NotBlank(message = UserResponseMessages.BIRTHDAY_REQUIRED)
    @Pattern(regexp = UserValidationRules.BIRTHDAY_FORMAT_PATTERN, message = UserResponseMessages.BIRTHDAY_FORMAT_INVALID)
    private String birth;

    @NotBlank(message = UserResponseMessages.EMAIL_REQUIRED)
    @Email(message = UserResponseMessages.EMAIL_INVALID)
    private String email;

    @NotBlank(message = UserResponseMessages.GENDER_REQUIRED)
    private String gender;

    private String job;

    @JsonIgnore
    @NotNull(message = UserResponseMessages.PROFILE_IMAGE_REQUIRED)
    private MultipartFile profileImage;

    @JsonIgnore
    public LocalDate parseBirthday() {
        return LocalDate.parse(birth, DateTimeFormatter.ofPattern(UserValidationRules.BIRTHDAY_DATE_FORMAT));
    }

    @JsonIgnore
    public Gender parseGender() {
        return Gender.from(gender);
    }
}
