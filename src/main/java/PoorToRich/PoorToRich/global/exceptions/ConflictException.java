package PoorToRich.PoorToRich.global.exceptions;

import PoorToRich.PoorToRich.global.response.Response;
import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {

    private final Response response;

    public ConflictException(Response response) {
        super(response.getMessage());
        this.response = response;
    }

}
