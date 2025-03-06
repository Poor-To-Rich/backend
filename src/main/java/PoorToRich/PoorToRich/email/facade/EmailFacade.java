package PoorToRich.PoorToRich.email.facade;

import PoorToRich.PoorToRich.email.request.EmailVerificationRequest;
import PoorToRich.PoorToRich.email.service.EmailVerificationService;
import PoorToRich.PoorToRich.email.service.MailService;
import PoorToRich.PoorToRich.email.util.VerificationCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailFacade {

    private final MailService mailService;
    private final EmailVerificationService verificationService;
    private final VerificationCodeGenerator codeGenerator;

    @Transactional
    public void sendVerificationCode(EmailVerificationRequest emailVerificationRequest) {
        String toEmail = emailVerificationRequest.email();
        String verificationPurpose = emailVerificationRequest.purpose();
        String verificationCode = codeGenerator.generate();

        verificationService.saveVerificationCode(toEmail, verificationPurpose, verificationCode);
        mailService.sendEmail(toEmail, verificationPurpose, verificationCode);
    }

}
