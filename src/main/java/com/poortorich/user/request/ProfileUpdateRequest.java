package com.poortorich.user.request;

import com.poortorich.user.constants.UserResponseMessages;
import com.poortorich.user.constants.UserValidationRules;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateRequest {

    private MultipartFile profileImage;

    private Boolean isDefaultProfile;

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

    @NotBlank(message = UserResponseMessages.BIRTHDAY_REQUIRED)
    @Pattern(regexp = UserValidationRules.BIRTHDAY_FORMAT_PATTERN, message = UserResponseMessages.BIRTHDAY_FORMAT_INVALID)
    private String birth;

    @NotBlank(message = UserResponseMessages.GENDER_REQUIRED)
    private String gender;

    private String job;
}
