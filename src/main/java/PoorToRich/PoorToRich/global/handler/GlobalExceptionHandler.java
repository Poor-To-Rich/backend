package PoorToRich.PoorToRich.global.handler;

import PoorToRich.PoorToRich.global.exceptions.AuthenticationException;
import PoorToRich.PoorToRich.global.exceptions.AuthorizationException;
import PoorToRich.PoorToRich.global.exceptions.BadRequestException;
import PoorToRich.PoorToRich.global.exceptions.ConflictException;
import PoorToRich.PoorToRich.global.exceptions.InternalServerErrorException;
import PoorToRich.PoorToRich.global.exceptions.NotFoundException;
import PoorToRich.PoorToRich.global.exceptions.UnauthorizedException;
import PoorToRich.PoorToRich.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {
        return BaseResponse.toResponseEntity(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<BaseResponse> handleAuthorizationException(AuthorizationException exception) {
        return BaseResponse.toResponseEntity(exception.getResponse());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<BaseResponse> handleAuthenticationException(AuthenticationException exception) {
        return BaseResponse.toResponseEntity(exception.getResponse());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BaseResponse> handleBadRequestException(BadRequestException exception) {
        return BaseResponse.toResponseEntity(exception.getResponse());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<BaseResponse> handleConflicException(ConflictException exception) {
        return BaseResponse.toResponseEntity(exception.getResponse());
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<BaseResponse> handleInternalServerErrorException(InternalServerErrorException exception) {
        return BaseResponse.toResponseEntity(exception.getResponse());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<BaseResponse> handleNotFoundException(NotFoundException exception) {
        return BaseResponse.toResponseEntity(exception.getResponse());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<BaseResponse> handleUnauthorizedException(UnauthorizedException exception) {
        return BaseResponse.toResponseEntity(exception.getResponse());
    }
}
