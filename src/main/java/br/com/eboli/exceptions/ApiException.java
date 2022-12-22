package br.com.eboli.exceptions;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Data
public class ApiException {

    private final HttpStatus status;
    private final String message;
    private final ZonedDateTime timestamp;

}