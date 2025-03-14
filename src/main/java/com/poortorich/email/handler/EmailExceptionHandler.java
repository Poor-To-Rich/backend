package com.poortorich.email.handler;

import com.poortorich.email.controller.MailController;
import com.poortorich.email.enums.EmailResponse;
import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.Response;
import jakarta.mail.internet.AddressException;
import java.util.Optional;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {MailController.class})
public class EmailExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleEmailMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        String errorMessage = Optional.ofNullable(exception.getBindingResult().getFieldError())
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse(EmailResponse.DEFAULT.getMessage());

        Response response = EmailResponse.getResponseByIdentifier(errorMessage);
        return BaseResponse.toResponseEntity(response);
    }

    @ExceptionHandler(MailSendException.class)
    public ResponseEntity<BaseResponse> handleMailSendException(MailSendException exception) {
        return BaseResponse.toResponseEntity(EmailResponse.EMAIL_SEND_FAILURE);
    }

    @ExceptionHandler(AddressException.class)
    public ResponseEntity<BaseResponse> handleAddressException(AddressException exception) {
        return BaseResponse.toResponseEntity(EmailResponse.EMAIL_NOT_FOUND);
    }
}
