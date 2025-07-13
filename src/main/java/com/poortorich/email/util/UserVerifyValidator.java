package com.poortorich.email.util;

import com.poortorich.email.enums.EmailVerificationType;
import com.poortorich.email.request.EmailVerificationRequest;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.user.entity.User;
import com.poortorich.user.repository.UserRepository;
import com.poortorich.user.response.enums.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserVerifyValidator {

    private final UserRepository userRepository;
    private final UserMailChecker userMailChecker;

    public void validateVerify(EmailVerificationRequest emailVerificationRequest) {
        String email = emailVerificationRequest.getEmail();
        String purpose = emailVerificationRequest.getPurpose();
        userMailChecker.checkByVerificationType(email, purpose);

        var type = EmailVerificationType.from(purpose);
        if (type == EmailVerificationType.CHANGE_PASSWORD) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException(UserResponse.USER_NOT_FOUND));

            if (!user.getUsername().equals(emailVerificationRequest.getUsername())) {
                throw new BadRequestException(UserResponse.USER_NOT_FOUND);
            }
        }
    }
}
