package com.poortorich.email.request;

import com.poortorich.email.constants.EmailResponseMessage;
import com.poortorich.email.validator.annotations.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailVerificationRequest {
    
    @NotBlank(message = EmailResponseMessage.EMAIL_INVALID)
    @Email
    private String email;

    @NotBlank(message = EmailResponseMessage.PURPOSE_INVALID)
    private String purpose;
}
