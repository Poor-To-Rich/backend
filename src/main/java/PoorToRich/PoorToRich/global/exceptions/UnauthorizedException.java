package PoorToRich.PoorToRich.global.exceptions;

import PoorToRich.PoorToRich.global.response.Response;
import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {

    private final Response response;

    public UnauthorizedException(Response response) {
        super(response.getMessage());
        this.response = response;
    }

}
