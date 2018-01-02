package com.vodafone.charging.accountservice.exception;

/**
 * Wrap any internal business logic exceptions in this.
 * Use when a http 500 scenario
 */

public class ApplicationLogicException extends RuntimeException {

    public ApplicationLogicException(String message) {
        super(message);
    }
    public ApplicationLogicException(String message, Throwable cause) {
        super(message, cause);
    }
}
