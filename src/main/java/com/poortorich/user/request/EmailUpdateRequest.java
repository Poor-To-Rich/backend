package com.poortorich.user.request;

import com.poortorich.email.validator.annotations.Email;
import com.poortorich.user.constants.UserResponseMessages;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailUpdateRequest {

    @Email
    @NotBlank(message = UserResponseMessages.EMAIL_REQUIRED)
    private String email;
}
