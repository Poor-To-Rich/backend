package PoorToRich.PoorToRich.email.service;

import PoorToRich.PoorToRich.email.enums.EmailTemplateType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender emailSender;

    private SimpleMailMessage createMailMessage(String email, String purpose, String verificationCode) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        EmailTemplateType emailTemplate = EmailTemplateType.getTemplateByPurpose(purpose);

        mailMessage.setTo(email);
        mailMessage.setSubject(emailTemplate.getSubject());
        mailMessage.setText(
                String.format(EmailTemplateType.BODY_TEMPLATE, emailTemplate.getDescription(), verificationCode));

        return mailMessage;
    }

    public void sendEmail(String email, String purpose, String verificationCode) {
        SimpleMailMessage mailMessage = this.createMailMessage(email, purpose, verificationCode);
        try {
            emailSender.send(mailMessage);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
