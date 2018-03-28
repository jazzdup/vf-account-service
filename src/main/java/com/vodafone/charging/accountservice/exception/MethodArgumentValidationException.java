package com.vodafone.charging.accountservice.exception;

public class MethodArgumentValidationException extends RuntimeException {

    public MethodArgumentValidationException(String message, Exception cause) {
        super(message, cause);
    }
    public MethodArgumentValidationException(String message) {
        super(message);
    }
}
