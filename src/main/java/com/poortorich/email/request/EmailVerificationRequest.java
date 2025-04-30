package com.poortorich.email.request;

import com.poortorich.email.constants.EmailResponseMessage;
import com.poortorich.email.validator.annotations.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailVerificationRequest {
    @NotBlank(message = EmailResponseMessage.INVALID_EMAIL)
    @Email
    private String email;

    @NotBlank(message = EmailResponseMessage.INVALID_PURPOSE)
    private String purpose;
}
