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
    
    @NotBlank(message = EmailResponseMessage.EMAIL_INVALID)
    @Email
    private String email;

    @NotBlank(message = EmailResponseMessage.PURPOSE_INVALID)
    private String purpose;

    @NotBlank(message = EmailResponseMessage.VERIFICATION_CODE_INVALID)
    @Length(min = 6, max = 6, message = EmailResponseMessage.VERIFICATION_CODE_INVALID)
    private String verificationCode;
}
