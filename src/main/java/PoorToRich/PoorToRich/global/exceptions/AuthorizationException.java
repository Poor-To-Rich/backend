package PoorToRich.PoorToRich.global.exceptions;

import PoorToRich.PoorToRich.global.response.Response;
import lombok.Getter;

@Getter
public class AuthorizationException extends RuntimeException {

    private final Response response;

    public AuthorizationException(Response response) {
        super(response.getMessage());
        this.response = response;
    }
}
