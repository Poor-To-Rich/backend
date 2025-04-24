package com.poortorich.email.request;

import com.poortorich.email.constants.EmailResponseMessage;
import com.poortorich.email.validator.annotations.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
@AllArgsConstructor
public class VerifyEmailCodeRequest {
    @NotBlank(message = EmailResponseMessage.INVALID_EMAIL)
    @Email
    private String email;

    @NotBlank(message = EmailResponseMessage.INVALID_PURPOSE)
    private String purpose;

    @NotBlank(message = EmailResponseMessage.INVALID_VERIFICATION_CODE)
    @Length(min = 6, max = 6, message = EmailResponseMessage.INVALID_VERIFICATION_CODE)
    private String verificationCode;
}
