package PoorToRich.PoorToRich.email.handler;

import PoorToRich.PoorToRich.email.controller.MailController;
import PoorToRich.PoorToRich.email.enums.EmailResponse;
import PoorToRich.PoorToRich.global.response.BaseResponse;
import PoorToRich.PoorToRich.global.response.Response;
import jakarta.mail.internet.AddressException;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {MailController.class})
public class EmailExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleEmailMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {
        StringBuilder errorMessageBuilder = new StringBuilder();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            errorMessageBuilder.append(error.getDefaultMessage()).append(" ");
        });

        Response response = EmailResponse.getResponseByIdentifier(errorMessageBuilder.toString());
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
