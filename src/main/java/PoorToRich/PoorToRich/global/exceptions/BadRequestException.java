package PoorToRich.PoorToRich.global.exceptions;

import PoorToRich.PoorToRich.global.response.Response;
import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    private final Response response;

    public BadRequestException(Response response) {
        super(response.getMessage());
        this.response = response;
    }

}
