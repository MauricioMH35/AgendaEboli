package br.com.eboli.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = { NotFoundException.class })
    public ResponseEntity<ApiException> handlerNotFoundException(NotFoundException e) {
        // payload
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        ApiException response = new ApiException(
            notFound,
            e.getMessage(),
            ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
        );

        // response entity
        return new ResponseEntity<>(response, notFound);
    }

    @ExceptionHandler(value = { IllegalArgumentException.class })
    public ResponseEntity<ApiException> handlerIllegalArgumentException(IllegalArgumentException e) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ApiException response = new ApiException(
                badRequest,
                e.getMessage(),
                ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
        );

        return new ResponseEntity<>(response, badRequest);
    }

    @ExceptionHandler(value = { ConflictException.class })
    public ResponseEntity<ApiException> handlerConflictEntityException(ConflictException e) {
        HttpStatus conflict = HttpStatus.CONFLICT;
        ApiException response = new ApiException(
                conflict,
                e.getMessage(),
                ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
        );

        return new ResponseEntity<>(response, conflict);
    }

    @ExceptionHandler(value = { InternalServereErrorException.class })
    public ResponseEntity<ApiException> handlerInternalServereErrorException(InternalServereErrorException e) {
        HttpStatus conflict = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException response = new ApiException(
                conflict,
                e.getMessage(),
                ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
        );

        return new ResponseEntity<>(response, conflict);
    }

    @ExceptionHandler(value = { BadRequestException.class })
    public ResponseEntity<ApiException> handlerBadRequestException(BadRequestException e) {
        HttpStatus conflict = HttpStatus.BAD_REQUEST;
        ApiException response = new ApiException(
                conflict,
                e.getMessage(),
                ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
        );

        return new ResponseEntity<>(response, conflict);
    }

}