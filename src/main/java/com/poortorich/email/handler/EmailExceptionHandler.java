package com.poortorich.email.handler;

import com.poortorich.email.controller.MailController;
import com.poortorich.email.response.enums.EmailResponse;
import com.poortorich.global.response.BaseResponse;
import jakarta.mail.internet.AddressException;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {MailController.class})
public class EmailExceptionHandler {

    @ExceptionHandler(MailSendException.class)
    public ResponseEntity<BaseResponse> handleMailSendException(MailSendException exception) {
        return BaseResponse.toResponseEntity(EmailResponse.EMAIL_SEND_FAILURE);
    }

    @ExceptionHandler(AddressException.class)
    public ResponseEntity<BaseResponse> handleAddressException(AddressException exception) {
        return BaseResponse.toResponseEntity(EmailResponse.EMAIL_NOT_FOUND);
    }
}
