package PoorToRich.PoorToRich.global.response;

import org.springframework.http.HttpStatus;

public interface Response {

    HttpStatus getHttpStatus();

    String getMessage();
}
