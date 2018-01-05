package com.vodafone.charging.accountservice.exception;

/**
 * Wrap any internal business logic exceptions in this.
 */

public class ApplicationLogicException extends RuntimeException {

    public ApplicationLogicException(final String message) {
        super(message);
    }
    public ApplicationLogicException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
