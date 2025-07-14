package com.poortorich.user.request;

import com.poortorich.email.constants.EmailResponseMessage;
import com.poortorich.email.validator.annotations.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FindUsernameRequest {

    @Email
    @NotBlank(message = EmailResponseMessage.EMAIL_INVALID)
    private String email;
}
