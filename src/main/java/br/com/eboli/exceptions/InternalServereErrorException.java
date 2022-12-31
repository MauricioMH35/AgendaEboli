package br.com.eboli.exceptions;

public class InternalServereErrorException extends RuntimeException {

    public InternalServereErrorException(String message) {
        super(message);
    }

    public InternalServereErrorException(String message, Throwable cause) {
        super(message, cause);
    }

}
