package PoorToRich.PoorToRich.global.exceptions;

import PoorToRich.PoorToRich.global.response.Response;
import lombok.Getter;

@Getter
public class InternalServerErrorException extends RuntimeException {

    private final Response response;

    public InternalServerErrorException(Response response) {
        super(response.getMessage());
        this.response = response;
    }

}
