package PoorToRich.PoorToRich.global.exceptions;

import PoorToRich.PoorToRich.global.response.Response;
import lombok.Getter;

@Getter
public class TooManyRequestException extends RuntimeException {

    Response response;

    public TooManyRequestException(Response response) {
        super(response.getMessage());
        this.response = response;
    }
}
