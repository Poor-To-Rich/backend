package com.poortorich.global.handler;

import com.poortorich.global.exceptions.AuthenticationException;
import com.poortorich.global.exceptions.AuthorizationException;
import com.poortorich.global.exceptions.BadRequestException;
import com.poortorich.global.exceptions.ConflictException;
import com.poortorich.global.exceptions.ForbiddenException;
import com.poortorich.global.exceptions.InternalServerErrorException;
import com.poortorich.global.exceptions.NotFoundException;
import com.poortorich.global.exceptions.TooManyRequestException;
import com.poortorich.global.exceptions.UnauthorizedException;
import com.poortorich.global.response.BaseResponse;

import java.util.Optional;

import com.poortorich.global.response.DataResponse;
import com.poortorich.global.response.ExceptionResponse;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String DEFAULT_ERROR_MESSAGE = "잘못된 요청입니다.";
    private static final String MISSING_PARAMETER = "요청 파라미터가 존재하지 않습니다.";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        FieldError fieldError = exception.getBindingResult().getFieldError();

        String errorMessage = Optional.ofNullable(exception.getBindingResult().getFieldError())
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse(DEFAULT_ERROR_MESSAGE);

        if (fieldError == null) {
            return DataResponse.toResponseEntity(HttpStatus.BAD_REQUEST, errorMessage, null);
        }

        return DataResponse.toResponseEntity(
                HttpStatus.BAD_REQUEST,
                errorMessage,
                ExceptionResponse.builder()
                        .field(fieldError.getField())
                        .build()
        );
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<BaseResponse> handleValidation(HandlerMethodValidationException exception) {
        String field = exception.getAllErrors().stream()
                .filter(error -> error instanceof FieldError)
                .map(error -> ((FieldError) error).getField())
                .findFirst()
                .orElse(null);

        String errorMessage = exception.getAllErrors().stream()
                .findFirst()
                .map(MessageSourceResolvable::getDefaultMessage)
                .orElse(DEFAULT_ERROR_MESSAGE);

        return DataResponse.toResponseEntity(
                HttpStatus.BAD_REQUEST,
                errorMessage,
                ExceptionResponse.builder()
                        .field(field)
                        .build()
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception) {
        return BaseResponse.toResponseEntity(HttpStatus.BAD_REQUEST, MISSING_PARAMETER);
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
    public ResponseEntity<BaseResponse> handleConflictException(ConflictException exception) {
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

    @ExceptionHandler(TooManyRequestException.class)
    public ResponseEntity<BaseResponse> handleTooManyRequestException(TooManyRequestException exception) {
        return BaseResponse.toResponseEntity(exception.getResponse());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<BaseResponse> handleForbiddenException(ForbiddenException exception) {
        return BaseResponse.toResponseEntity(exception.getResponse());
    }
}
