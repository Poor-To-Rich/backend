package com.poortorich.email.util;

import com.poortorich.email.facade.EmailFacade;
import com.poortorich.email.response.enums.EmailResponse;
import com.poortorich.global.response.BaseResponse;
import com.poortorich.global.response.DataResponse;
import com.poortorich.global.response.Response;
import org.springframework.http.ResponseEntity;

public class EmailResponseMapper {

    public static ResponseEntity<BaseResponse> mapToResponseEntity(
            Response response,
            String email,
            EmailFacade emailFacade
    ) {
        if (response == EmailResponse.EMAIL_AUTH_REQUEST_BLOCKED) {
            return DataResponse.toResponseEntity(response, emailFacade.getBlockExpired(email));
        }
        if (response == EmailResponse.VERIFICATION_CODE_SENT) {
            return DataResponse.toResponseEntity(response, emailFacade.getResendCodeAttempts(email));
        }
        if (response == EmailResponse.VERIFICATION_CODE_INVALID) {
            return DataResponse.toResponseEntity(response, emailFacade.getEmailVerificationAttempts(email));
        }
        return BaseResponse.toResponseEntity(response);
    }
}
