package com.poortorich.email.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
@AllArgsConstructor
public class VerifyEmailCodeRequest {
    @NotBlank(message = "invalid_email")
    @Email(message = "invalid_email")
    private String email;

    @NotBlank(message = "invalid_purpose")
    private String purpose;

    @NotBlank(message = "invalid_verification_code")
    @Length(min = 6, max = 6, message = "invalid_verification_code")
    private String verificationCode;
}
