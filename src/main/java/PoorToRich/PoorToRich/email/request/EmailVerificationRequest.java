package PoorToRich.PoorToRich.email.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailVerificationRequest(
        @NotBlank(message = "invalid_email") @Email(message = "invalid_email") String email,
        @NotBlank(message = "invalid_purpose") String purpose) {
}
