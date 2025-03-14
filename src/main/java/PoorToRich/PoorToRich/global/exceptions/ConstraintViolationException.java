package PoorToRich.PoorToRich.global.exceptions;

import PoorToRich.PoorToRich.global.response.Response;
import lombok.Getter;

@Getter
public class ConstraintViolationException extends RuntimeException {

    private final Response response;

    public ConstraintViolationException(Response response) {
        super(response.getMessage());
        this.response = response;
    }
}
